package dev.efnilite.worldserver.group;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.vilib.util.elevator.GitElevator;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GeneralHandler implements EventWatcher {

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GitElevator elevator = WorldServer.getPlugin().getElevatorInstance();

        if (player.isOp() && elevator != null && elevator.isOutdated()) {
            Util.send(player, "");
            Util.send(player, WorldServer.MESSAGE_PREFIX + "Your WorldServer version is outdated. Please update!");
            Util.send(player, "");
        }

        WorldPlayer.register(player);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        WorldPlayer.unregister(event.getPlayer(), true);
    }
}
