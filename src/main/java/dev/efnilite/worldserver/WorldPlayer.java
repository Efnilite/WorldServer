package dev.efnilite.worldserver;

import com.google.gson.annotations.Expose;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.worldserver.config.ConfigValue;
import dev.efnilite.worldserver.eco.BalCache;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldPlayer {
    @Expose
    public boolean spyMode;

    @Expose
    public Map<String, Double> balances;

    private final Player player;

    private static final Map<UUID, WorldPlayer> players = new HashMap<>();

    public WorldPlayer(Player player) {
        this.player = player;
    }

    /**
     * Registers a player
     *
     * @param   player
     *          The player
     *
     * @return the WorldPlayer instance
     */
    public static WorldPlayer register(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        WorldPlayer oldWp = players.get(player.getUniqueId());
        if (oldWp == null) {
            WorldPlayer wp = read(player);

            players.put(uuid, wp);
            return wp;
        } else {
            return oldWp;
        }
    }

    /**
     * Unregisters a player
     *
     * @param   player
     *          The player
     */
    public static void unregister(@NotNull Player player, boolean saveAsync) {
        UUID uuid = player.getUniqueId();
        WorldPlayer wp = players.get(player.getUniqueId());
        if (wp == null) {
            return;
        }

        wp.save(saveAsync);

        players.remove(uuid);
    }

    /**
     * Gets a player from the bukkit player instance
     *
     * @param   player
     *          The player
     *
     * @return the WorldPlayer instance
     */
    @NotNull
    public static WorldPlayer getPlayer(@NotNull Player player) {
        WorldPlayer p = players.get(player.getUniqueId());
        return p != null ? p : register(player);
    }

    /**
     * Saves this player's file
     *
     * @param   async
     *          True if saving should be async
     */
    public void save(boolean async) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    File file = new File(WorldServer.getPlugin().getDataFolder(), "players/" + player.getUniqueId() + ".json");
                    if (!file.exists()) {
                        File folder = new File(WorldServer.getPlugin().getDataFolder(), "players");
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        file.createNewFile();
                    }

                    FileWriter writer = new FileWriter(file);
                    WorldServer.getGson().toJson(WorldPlayer.this, writer);

                    writer.flush();
                    writer.close();
                } catch (Throwable throwable) {
                    WorldServer.logging().stack("Error while saving file of player", "Please report this error to the developer", throwable);
                }
            }
        };

        if (async) {
            Task.create(WorldServer.getPlugin())
                    .async()
                    .execute(runnable)
                    .run();
        } else {
            runnable.run();
        }
    }

    /**
     * Reads a player's file
     *
     * @param   player
     *          The player
     *
     * @return the instance of the player
     */
    @Nullable
    public static WorldPlayer read(Player player) {
        try {
            File file = new File(WorldServer.getPlugin().getDataFolder(), "players/" + player.getUniqueId() + ".json");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileReader reader = new FileReader(file);
            WorldPlayer container = WorldServer.getGson().fromJson(reader, WorldPlayer.class);
            WorldPlayer newWp = new WorldPlayer(player);

            if (container == null) {
                WorldPlayer def = new WorldPlayer(player);

                def.spyMode = false;
                def.balances = new HashMap<>();

                def.save(false);
                return def;
            }

            newWp.spyMode = container.spyMode;
            newWp.balances = container.balances != null ? container.balances : new HashMap<>();

            reader.close();
            return newWp;
        } catch (Throwable throwable) {
            WorldServer.logging().stack("Error while reading file of player", "Please report this error to the developer", throwable);
            return null;
        }
    }

    public void send(String... message) {
        for (String s : message) {
            Util.send(player, s);
        }
    }

    public void setBalance(double amount, String group) {
        if (ConfigValue.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        balances.put(group, amount);
        BalCache.save(player.getUniqueId(), group, amount);
    }

    public double getBalance() {
        String group = getWorldGroup();
        if (ConfigValue.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        if (!balances.containsKey(group)) {
            balances.put(group, ConfigValue.ECONOMY_STARTING_AMOUNT.getOrDefault(group, 1D));
        }
        return balances.get(group);
    }

    public double getBalance(String group) {
        if (ConfigValue.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        if (!balances.containsKey(group)) {
            balances.put(group, ConfigValue.ECONOMY_STARTING_AMOUNT.getOrDefault(group, 1D));
        }
        return balances.get(group);
    }

    public void withdraw(double amount) {
        String group = getWorldGroup();
        if (ConfigValue.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        withdraw(group, amount);
    }

    public void withdraw(String group, double amount) {
        if (ConfigValue.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        if (!balances.containsKey(group)) {
            balances.put(group, ConfigValue.ECONOMY_STARTING_AMOUNT.getOrDefault(group, 1D));
        }
        double updated = balances.get(group) - amount;
        BalCache.save(player.getUniqueId(), group, updated);

        balances.put(group, updated);
    }

    public void deposit(double amount) {
        String group = getWorldGroup();
        if (ConfigValue.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        deposit(group, amount);
    }

    public void deposit(String group, double amount) {
        if (ConfigValue.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        if (!balances.containsKey(group)) {
            balances.put(group, ConfigValue.ECONOMY_STARTING_AMOUNT.getOrDefault(group, 1D));
        }
        double updated = balances.get(group) + amount;
        BalCache.save(player.getUniqueId(), group, updated);

        balances.put(group, updated);
    }

    public static Map<UUID, WorldPlayer> getPlayers() {
        return players;
    }

    public World getWorld() {
        return player.getWorld();
    }

    public Player getPlayer() {
        return player;
    }

    public String getWorldGroup() {
        return ConfigValue.getGroupFromWorld(getWorld());
    }

    public boolean spyMode() {
        return spyMode;
    }
}