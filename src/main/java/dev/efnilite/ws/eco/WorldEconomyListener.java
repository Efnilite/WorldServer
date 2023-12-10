package dev.efnilite.ws.eco;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.ws.WorldPlayer;
import dev.efnilite.ws.config.Option;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class WorldEconomyListener implements EventWatcher {

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!Option.ECONOMY_ENABLED || !Option.ECONOMY_SWITCH_NOTIFICATION) {
            return;
        }

        WorldPlayer player = WorldPlayer.getPlayer(event.getPlayer());

        player.send(Option.ECONOMY_SWITCH_FORMAT.replace("%amount%", Double.toString(player.getBalance())));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void join(PlayerJoinEvent event) {
        if (!Option.ECONOMY_ENABLED || !Option.ECONOMY_SWITCH_NOTIFICATION) {
            return;
        }

        WorldPlayer player = WorldPlayer.getPlayer(event.getPlayer());

        player.send(Option.ECONOMY_SWITCH_FORMAT.replace("%amount%", Double.toString(player.getBalance())));
    }
}