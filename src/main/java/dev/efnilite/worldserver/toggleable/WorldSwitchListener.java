package dev.efnilite.worldserver.toggleable;

import dev.efnilite.worldserver.config.Option;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class WorldSwitchListener extends Toggleable implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!Option.TAB_ENABLED) {
            return;
        }
        Player player = event.getPlayer();

        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (!pl.getWorld().getName().equals(player.getWorld().getName())) {
                visibilityHandler.hide(pl, player);
            } else {
                visibilityHandler.show(pl, player);
            }
        }
    }

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!Option.TAB_ENABLED) {
            return;
        }
        Player player = event.getPlayer();

        // Previous world, uses fromWorld
        World fromWorld = event.getFrom();
        List<Player> fromPlayers = getPlayersInWorldGroup(fromWorld);

        // Current world, uses currentWorld
        World toWorld = player.getWorld();
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