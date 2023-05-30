package dev.efnilite.worldserver;

import com.google.gson.annotations.Expose;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.eco.BalCache;
import dev.efnilite.worldserver.util.GroupUtil;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldPlayer {

    @Expose
    public boolean spyMode = false;

    @Expose
    public Map<String, Double> balances = new HashMap<>();

    public final Player player;

    public static final Map<UUID, WorldPlayer> PLAYERS = new HashMap<>();

    public WorldPlayer(Player player) {
        this.player = player;
    }

    /**
     * Registers a player
     *
     * @param player The player
     * @return the WorldPlayer instance
     */
    public static WorldPlayer register(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        WorldPlayer oldWp = PLAYERS.get(player.getUniqueId());

        if (oldWp == null) {
            WorldPlayer wp = read(player);

            PLAYERS.put(uuid, wp);
            return wp;
        } else {
            return oldWp;
        }
    }

    /**
     * Unregisters a player
     *
     * @param player The player
     */
    public static void unregister(@NotNull Player player, boolean saveAsync) {
        UUID uuid = player.getUniqueId();
        WorldPlayer wp = PLAYERS.get(player.getUniqueId());

        if (wp == null) {
            return;
        }

        wp.save(saveAsync);

        PLAYERS.remove(uuid);
    }

    /**
     * Gets a player from the bukkit player instance
     *
     * @param player The player
     * @return the WorldPlayer instance
     */
    @NotNull
    public static WorldPlayer getPlayer(@NotNull Player player) {
        WorldPlayer p = PLAYERS.get(player.getUniqueId());
        return p != null ? p : register(player);
    }

    /**
     * Saves this player's file
     *
     * @param async True if saving should be async
     */
    public void save(boolean async) {
        if (async) {
            Task.create(WorldServer.getPlugin()).async().execute(this::_save).run();
        } else {
            _save();
        }
    }

    private void _save() {
        File file = WorldServer.getInFolder("players/" + player.getUniqueId() + ".json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            WorldServer.getGson().toJson(WorldPlayer.this, writer);

            writer.flush();
        } catch (Throwable throwable) {
            WorldServer.logging().stack("Error while saving file of player", "Please report this error to the developer", throwable);
        }
    }

    /**
     * Reads a player's file
     *
     * @param player The player
     * @return the instance of the player
     */
    public static @NotNull WorldPlayer read(Player player) {
        File file = WorldServer.getInFolder(String.format("players/%s.json", player.getUniqueId()));

        WorldPlayer wp = new WorldPlayer(player);

        if (!file.exists()) {
            wp.save(true);
            return wp;
        }

        try (FileReader reader = new FileReader(file)) {
            WorldPlayer container = WorldServer.getGson().fromJson(reader, WorldPlayer.class);

            if (container == null) {
                wp.save(true);
                return wp;
            }

            wp.spyMode = container.spyMode;
            wp.balances = container.balances != null ? container.balances : new HashMap<>();

            return wp;
        } catch (Throwable throwable) {
            WorldServer.logging().stack("Error while reading file of player", "Please report this error to the developer", throwable);
            return wp;
        }
    }

    public void send(String message) {
        Util.send(player, message);
    }

    public void setBalance(double amount, String group) {
        if (Option.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        balances.put(group, amount);
        BalCache.save(player.getUniqueId(), group, amount);
    }

    /**
     * Returns the balance of a player.
     * Defaults to "global" group if global economy is enabled.
     * Defaults to "economy starting amount" if the user has no registered balance.
     *
     * @return the balance of a player.
     */
    public double getBalance() {
        return getBalance(getWorldGroup());
    }

    /**
     * Returns the balance of a player from a specific group.
     *
     * @param group The group name
     * @return the balance of a player from a specific group.
     */
    public double getBalance(String group) {
        if (Option.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        if (!balances.containsKey(group)) {
            balances.put(group, Option.ECONOMY_STARTING_AMOUNT.getOrDefault(group, 1D));
        }
        return balances.get(group);
    }

    public void withdraw(double amount) {
        String group = getWorldGroup();
        if (Option.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        withdraw(group, amount);
    }

    public void withdraw(String group, double amount) {
        if (Option.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        if (!balances.containsKey(group)) {
            balances.put(group, Option.ECONOMY_STARTING_AMOUNT.getOrDefault(group, 1D));
        }
        double updated = balances.get(group) - amount;
        BalCache.save(player.getUniqueId(), group, updated);

        balances.put(group, updated);
    }

    public void deposit(double amount) {
        String group = getWorldGroup();
        if (Option.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        deposit(group, amount);
    }

    public void deposit(String group, double amount) {
        if (Option.ECONOMY_GLOBAL_ENABLED) {
            group = "global";
        }

        if (!balances.containsKey(group)) {
            balances.put(group, Option.ECONOMY_STARTING_AMOUNT.getOrDefault(group, 1D));
        }
        double updated = balances.get(group) + amount;
        BalCache.save(player.getUniqueId(), group, updated);

        balances.put(group, updated);
    }

    public String getWorldGroup() {
        return GroupUtil.getGroupFromWorld(player.getWorld());
    }
}