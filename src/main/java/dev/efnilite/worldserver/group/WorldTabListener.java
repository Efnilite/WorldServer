package dev.efnilite.worldserver.group;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.util.VisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

/**
 * Manages tab.
 */
public class WorldTabListener implements EventWatcher {

    private final VisibilityHandler visibilityHandler = WorldServer.getVisibilityHandler();

    @EventHandler(priority = EventPriority.HIGH)
    public void join(PlayerJoinEvent event) {
        if (!Option.TAB_ENABLED) {
            return;
        }

        Player player = event.getPlayer();

        List<Player> inGroup = GroupUtil.getPlayersInWorldGroup(player.getWorld());

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (inGroup.contains(other)) {
                visibilityHandler.show(player, other);
                visibilityHandler.show(other, player);
            } else {
                visibilityHandler.hide(player, other);
                visibilityHandler.hide(other, player);
            }
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (!Option.TAB_ENABLED) {
            return;
        }

        Player player = event.getPlayer();

        for (Player other : Bukkit.getOnlinePlayers()) {
            visibilityHandler.show(player, other);
            visibilityHandler.show(other, player);
        }
    }

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!Option.TAB_ENABLED) {
            return;
        }

        Player player = event.getPlayer();

        World fromWorld = event.getFrom(); // previous world
        World toWorld = player.getWorld(); // current world

        List<Player> fromPlayers = GroupUtil.getPlayersInWorldGroup(fromWorld);
        List<Player> toPlayers = GroupUtil.getPlayersInWorldGroup(toWorld);

        for (Player other : fromPlayers) { // hide from previous world
            visibilityHandler.hide(other, player);
            visibilityHandler.hide(player, other);
        }

        for (Player other : toPlayers) { // show to current world
            visibilityHandler.show(other, player);
            visibilityHandler.show(player, other);
        }
    }
}