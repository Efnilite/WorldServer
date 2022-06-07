package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldEconomyListener implements EventWatcher {

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!ConfigValue.ECONOMY_ENABLED) {
            return;
        }
        Player p = event.getPlayer();
        WorldPlayer player = WorldPlayer.getPlayer(p);

        // switch notification
        if (ConfigValue.ECONOMY_SWITCH_NOTIFICATION) {
            player.send(ConfigValue.ECONOMY_SWITCH_FORMAT.replace("%amount%", Double.toString(player.getBalance())));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void join(PlayerChangedWorldEvent event) {
        if (!ConfigValue.ECONOMY_ENABLED) {
            return;
        }
        Player p = event.getPlayer();
        WorldPlayer player = WorldPlayer.getPlayer(p);

        // switch notification
        if (ConfigValue.ECONOMY_SWITCH_NOTIFICATION) {
            player.send(ConfigValue.ECONOMY_SWITCH_FORMAT.replace("%amount%", Double.toString(player.getBalance())));
        }
    }
}