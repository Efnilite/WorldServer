package dev.efnilite.worldserver.group;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.ConfigValue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class WorldEconomyListener implements EventWatcher {

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!ConfigValue.ECONOMY_ENABLED || !ConfigValue.ECONOMY_SWITCH_NOTIFICATION) {
            return;
        }

        WorldPlayer player = WorldPlayer.getPlayer(event.getPlayer());

        player.send(ConfigValue.ECONOMY_SWITCH_FORMAT.replace("%amount%", Double.toString(player.getBalance())));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void join(PlayerJoinEvent event) {
        if (!ConfigValue.ECONOMY_ENABLED || !ConfigValue.ECONOMY_SWITCH_NOTIFICATION) {
            return;
        }

        WorldPlayer player = WorldPlayer.getPlayer(event.getPlayer());

        player.send(ConfigValue.ECONOMY_SWITCH_FORMAT.replace("%amount%", Double.toString(player.getBalance())));
    }
}