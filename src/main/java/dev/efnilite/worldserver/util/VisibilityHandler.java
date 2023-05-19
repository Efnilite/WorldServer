package dev.efnilite.worldserver.util;

import dev.efnilite.vilib.util.Version;
import dev.efnilite.worldserver.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Handler for visibility of players
 */
public interface VisibilityHandler {

    /**
     * Shows a player to another one
     *
     * @param player The player
     * @param show   The player that will be shown to {@code player}
     */
    void show(Player player, Player show);

    /**
     * Hides a player from another one
     *
     * @param player The player
     * @param hide   The player that will be hidden from {@code player}
     */
    void hide(Player player, Player hide);

    static VisibilityHandler getInstance() {
        switch (Version.getVersion()) {
            case V1_19:
            case V1_18:
            case V1_17:
            case V1_16:
            case V1_15:
            case V1_14:
            case V1_13:
                return new VisibilityHandler_v1_13();
            case V1_12:
            case V1_11:
            case V1_10:
            case V1_9:
            case V1_8:
                return new VisibilityHandler_v1_8();
            default:
                WorldServer.logging().error("Unsupported version! Please upgrade your server :(");
                Bukkit.getPluginManager().disablePlugin(WorldServer.getPlugin());
                return new VisibilityHandler_v1_13();
        }
    }
}
