package dev.efnilite.worldserver;

import com.google.gson.annotations.Expose;
import dev.efnilite.fycore.chat.Message;
import dev.efnilite.fycore.util.Logging;
import dev.efnilite.fycore.util.Task;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class WorldPlayer {

    @Expose
    private boolean spyMode;

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
        WorldPlayer oldWp = getPlayer(player);
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
    public static void unregister(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        WorldPlayer wp = getPlayer(player);
        if (wp == null) {
            return;
        }

        wp.save(true);

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
    @Nullable
    public static WorldPlayer getPlayer(@NotNull Player player) {
        return players.get(player.getUniqueId());
    }

    /**
     * Saves this player's file
     *
     * @param   async
     *          True if saving should be async
     */
    public void save(boolean async) {
        if (async) {
            new Task()
                    .async()
                    .execute(() -> {
                        try {
                            File file = new File(WorldServer.getFyPlugin().getDataFolder() + "/players/" + player.getUniqueId() + ".json");

                            if (!file.exists()) {
                                file.createNewFile();
                            }

                            FileWriter writer = new FileWriter(file.getAbsolutePath());
                            WorldServer.getGson().toJson(this, writer);

                            writer.flush();
                            writer.close();
                        } catch (Throwable throwable) {
                            Logging.stack("Error while saving file of player", "Please report this error to the developer", throwable);
                        }
                    }).run();
        } else {
            try {
                File file = new File(WorldServer.getFyPlugin().getDataFolder() + "/players/" + player.getUniqueId() + ".json");

                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter writer = new FileWriter(file.getAbsolutePath());
                WorldServer.getGson().toJson(this, writer);

                writer.flush();
                writer.close();
            } catch (Throwable throwable) {
                Logging.stack("Error while saving file of player", "Please report this error to the developer", throwable);
            }
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
            File file = new File(WorldServer.getFyPlugin().getDataFolder() + "/players/" + player.getUniqueId() + ".json");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileReader reader = new FileReader(file);
            WorldPlayer valueContainer = WorldServer.getGson().fromJson(reader, WorldPlayer.class);
            WorldPlayer newWp = new WorldPlayer(player);

            if (valueContainer == null) {
                WorldPlayer def = new WorldPlayer(player);

                def.spyMode = false;

                def.save(false);
                return def;
            }

            newWp.setSpyMode(valueContainer.spyMode);

            reader.close();
            return newWp;
        } catch (Throwable throwable) {
            Logging.stack("Error while reading file of player", "Please report this error to the developer", throwable);
            return null;
        }
    }

    public void send(String... message) {
        for (String s : message) {
            Message.send(player, s);
        }
    }

    public static Map<UUID, WorldPlayer> getPlayers() {
        return players;
    }

    public void setSpyMode(boolean spyMode) {
        this.spyMode = spyMode;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean spyMode() {
        return spyMode;
    }
}
