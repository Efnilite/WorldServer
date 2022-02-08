package dev.efnilite.worldserver.toggleable;

import dev.efnilite.fycore.chat.Message;
import dev.efnilite.fycore.event.EventWatcher;
import dev.efnilite.fycore.util.Version;
import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

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
    }
}
