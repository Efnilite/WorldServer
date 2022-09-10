package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GeneralHandler implements EventWatcher {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() && WorldServer.getPlugin().getElevatorInstance().isOutdated()) {
            Util.send(player, "");
            Util.send(player, WorldServer.MESSAGE_PREFIX + "Your WorldServer version is outdated. Please update!");
            Util.send(player, "");
        }

        WorldPlayer.register(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        WorldPlayer.unregister(event.getPlayer());
    }
}
