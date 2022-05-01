package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.Option;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldEconomyListener implements EventWatcher {

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        WorldPlayer player = WorldPlayer.getPlayer(event.getPlayer());

        if (player == null) {
            player = WorldPlayer.register(event.getPlayer());
        }

        if (Option.ECONOMY_SWITCH_NOTIFICATION) {
            player.send(Option.ECONOMY_SWITCH_FORMAT.replace("%amount%", Double.toString(player.getBalance())));
        }
    }
}