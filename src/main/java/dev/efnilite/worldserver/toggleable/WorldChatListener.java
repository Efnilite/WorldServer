package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.vault.VChat;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class WorldChatListener extends Toggleable implements EventWatcher {

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        if (!Option.CHAT_ENABLED) {
            return;
        }
        Player player = event.getPlayer();
        String message = event.getMessage();
        String prefix = Option.GLOBAL_CHAT_PREFIX;

        // Global chat handling
        if (Option.GLOBAL_CHAT_ENABLED && message.length() > prefix.length() && message.substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
            event.setFormat(getChatFormatted(player, Option.GLOBAL_CHAT_FORMAT));
            event.setMessage(message.replaceFirst(prefix, ""));
            return;
        }

        World world = player.getWorld();

        // Update recipients with world groups
        List<Player> sendTo = getPlayersInWorldGroup(world);

        event.getRecipients().clear();
        event.getRecipients().addAll(sendTo);

        String spy = Option.SPY_FORMAT
                .replace("%world%", world.getName())
                .replace("%player%", player.getName())
                .replace("%message%", event.getMessage());
        for (WorldPlayer wp : WorldPlayer.getPlayers().values()) {
            if (wp.spyMode() && !sendTo.contains(wp.getPlayer())) {
                wp.send(spy);
            }
        }

        // Update possible formatting for groups and single worlds
        String format;
        String group = Option.getGroupFromWorld(world);
        if (group.equals("")) {
            format = Option.CHAT_FORMAT.get(world.getName());
        } else {
            format = Option.CHAT_FORMAT.get(group);
        }
        if (format != null) {
            event.setFormat(getChatFormatted(player, format));
        }
    }

    private String getChatFormatted(Player player, String format) {
        return Message.parseFormatting(format
                .replace("%player%", Option.CHAT_AFFIXES ? VChat.getPrefix(player) + "%s" + VChat.getSuffix(player) : "%s")
                .replace("%message%", "%s"));
    }
}