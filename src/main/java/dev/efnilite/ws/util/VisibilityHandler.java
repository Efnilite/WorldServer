package dev.efnilite.ws.util;

import dev.efnilite.ws.WorldServer;
import org.bukkit.entity.Player;

// from 1.13 to 1.18
public class VisibilityHandler {

    /**
     * Shows a player to another one
     *
     * @param player The player
     * @param show   The player that will be shown to {@code player}
     */
    public void show(Player player, Player show) {
        player.showPlayer(WorldServer.getPlugin(), show);
    }

    /**
     * Hides a player from another one
     *
     * @param player The player
     * @param hide   The player that will be hidden from {@code player}
     */
    public void hide(Player player, Player hide) {
        player.hidePlayer(WorldServer.getPlugin(), hide);
    }
}
