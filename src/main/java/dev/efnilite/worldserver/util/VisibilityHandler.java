package dev.efnilite.worldserver.util;

import dev.efnilite.worldserver.WorldServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Handler for visibility of players
 */
public abstract class VisibilityHandler {

    protected final Plugin plugin;

    public VisibilityHandler() {
        this.plugin = WorldServer.getPlugin();
    }

    /**
     * Shows a player to another one
     *
     * @param   player
     *          The player
     *
     * @param   show
     *          The player that will be shown to {@code player}
     */
    public abstract void show(Player player, Player show);

    /**
     * Hides a player from another one
     *
     * @param   player
     *          The player
     *
     * @param   hide
     *          The player that will be hidden from {@code player}
     */
    public abstract void hide(Player player, Player hide);

}
