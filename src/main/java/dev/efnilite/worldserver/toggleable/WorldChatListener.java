package dev.efnilite.worldserver.toggleable;

import dev.efnilite.fycore.event.EventWatcher;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.util.Util;
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
        String message = event.getMessage();
        String prefix = Option.GLOBAL_CHAT_PREFIX;

        // Global chat handling
        if (Option.GLOBAL_CHAT_ENABLED && message.length() > prefix.length() && message.substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
            event.setFormat(getChatFormatted(Option.GLOBAL_CHAT_FORMAT));
            event.setMessage(message.replaceFirst(prefix, ""));
            return;
        }

        World world = event.getPlayer().getWorld();

        // Update recipients with world groups
        List<Player> sendTo = getPlayersInWorldGroup(world);

        event.getRecipients().clear();
        event.getRecipients().addAll(sendTo);

        // Update possible formatting for groups and single worlds
        String format;
        String group = Option.getGroupFromWorld(world);
        if (group.equals("")) {
            format = Option.CHAT_FORMAT.get(world.getName());
        } else {
            format = Option.CHAT_FORMAT.get(group);
        }
        if (format != null) {
            event.setFormat(getChatFormatted(format));
        }
    }

    private String getChatFormatted(String format) {
        return Util.colour(format
                .replace("%player%", "%s")
                .replace("%message%", "%s"));
    }
}