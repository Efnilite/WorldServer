package dev.efnilite.worldserver.util;

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

}
