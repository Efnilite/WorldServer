package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.vilib.util.Version;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.WorldServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GeneralHandler implements EventWatcher {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() && WorldServer.getElevator().isOutdated()) {
            if (Version.isHigherOrEqual(Version.V1_16)) {
                Message.send(player, "");
                Message.send(player,
                        WorldServer.MESSAGE_PREFIX + "Your version is outdated. " +
                                "Please visit the Spigot page to update.");
                Message.send(player, "");
            } else {
                Message.send(player, "");
                Message.send(player, WorldServer.MESSAGE_PREFIX + "Your WorldServer version is outdated. Please update!");
                Message.send(player, "");
            }
        }

        WorldPlayer.register(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        WorldPlayer.unregister(event.getPlayer());
    }
}
