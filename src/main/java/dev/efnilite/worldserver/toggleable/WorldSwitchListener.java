package dev.efnilite.worldserver.toggleable;

import dev.efnilite.worldserver.config.Option;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class WorldSwitchListener extends Toggleable implements Listener {

    public WorldSwitchListener(boolean enabled) {
        super(enabled);
    }

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
        List<Player> previous = event.getFrom().getPlayers();
        List<Player> current = player.getWorld().getPlayers();
        for (Player previousPlayer : previous) { // hide from previous world
            visibilityHandler.hide(previousPlayer, player);
            visibilityHandler.hide(player, previousPlayer);
        }

        for (Player currentPlayer : current) { // show to current world
            visibilityHandler.show(currentPlayer, player);
            visibilityHandler.show(player, currentPlayer);
        }
    }
}