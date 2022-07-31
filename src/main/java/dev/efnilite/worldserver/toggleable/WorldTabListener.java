package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class WorldTabListener extends Toggleable implements EventWatcher {

    @EventHandler(priority = EventPriority.HIGH)
    public void join(PlayerJoinEvent event) {
        if (!ConfigValue.TAB_ENABLED) {
            return;
        }
        Player player = event.getPlayer();

        List<Player> inGroup = getPlayersInWorldGroup(player.getWorld());
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (inGroup.contains(pl)) {
                visibilityHandler.show(player, pl);
                visibilityHandler.show(pl, player);
            } else {
                visibilityHandler.hide(player, pl);
                visibilityHandler.hide(pl, player);
            }
        }
    }

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!ConfigValue.TAB_ENABLED) {
            return;
        }
        Player player = event.getPlayer();

        // Previous world, uses fromWorld
        World fromWorld = event.getFrom();

        // Current world, uses currentWorld
        World toWorld = player.getWorld();

        List<Player> fromPlayers = getPlayersInWorldGroup(fromWorld);
        List<Player> toPlayers = getPlayersInWorldGroup(toWorld);

        for (Player previousPlayer : fromPlayers) { // hide from previous world
            visibilityHandler.hide(previousPlayer, player);
            visibilityHandler.hide(player, previousPlayer);
        }

        for (Player currentPlayer : toPlayers) { // show to current world
            visibilityHandler.show(currentPlayer, player);
            visibilityHandler.show(player, currentPlayer);
        }
    }
}