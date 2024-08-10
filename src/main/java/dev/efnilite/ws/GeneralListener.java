package dev.efnilite.ws;

import dev.efnilite.vilib.event.EventWatcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GeneralListener implements EventWatcher {

    @EventHandler
    public void join(PlayerJoinEvent event) {
        WorldPlayer.register(event.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        WorldPlayer.unregister(event.getPlayer(), true);
    }
}
