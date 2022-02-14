package dev.efnilite.worldserver.toggleable;

import dev.efnilite.fycore.chat.Message;
import dev.efnilite.fycore.event.EventWatcher;
import dev.efnilite.fycore.util.Version;
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

        if (player.isOp() && WorldServer.IS_OUTDATED) {
            if (Version.isHigherOrEqual(Version.V1_16)) {
                Message.send(player, "");
                Message.send(player,
                        WorldServer.MESSAGE_PREFIX + "Your version is outdated. " +
                                "Please <underline>visit the Spigot page<reset><gray> to update.");
                Message.send(player, "");
            } else {
                Message.send(player, "");
                Message.send(player, WorldServer.MESSAGE_PREFIX + "Your WorldServer version is outdated. Please update!");
                Message.send(player, "");
            }
        }

        if (player.hasPermission("ws.menu")) {
            WorldPlayer.register(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        WorldPlayer.unregister(event.getPlayer());
    }
}
