package dev.efnilite.worldserver.util;

import dev.efnilite.worldserver.WorldServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Handler for visibility of players
 */
public abstract class VisibilityHandler {

    protected Plugin plugin;

    public VisibilityHandler() {
        this.plugin = WorldServer.getInstance();
    }

    public abstract void show(Player player, Player show);

    public abstract void hide(Player player, Player hide);

}
