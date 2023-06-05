package dev.efnilite.worldserver;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.vilib.util.elevator.GitElevator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GeneralListener implements EventWatcher {

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GitElevator elevator = WorldServer.getPlugin().getElevatorInstance();

        if (player.isOp() && elevator != null && elevator.isOutdated()) {
            WorldServerCommand.send(player, "");
            WorldServerCommand.send(player, "%sYour WorldServer version is outdated. Please update!".formatted(WorldServer.MESSAGE_PREFIX));
            WorldServerCommand.send(player, "");
        }

        WorldPlayer.register(player);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        WorldPlayer.unregister(event.getPlayer(), true);
    }
}
