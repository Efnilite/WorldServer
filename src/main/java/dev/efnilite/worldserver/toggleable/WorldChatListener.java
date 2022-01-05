package dev.efnilite.worldserver.toggleable;

import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class WorldChatListener extends Toggleable implements Listener {

    public WorldChatListener(boolean enabled) {
        super(enabled);
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        if (!Option.CHAT_ENABLED) {
            return;
        }
        String message = event.getMessage();
        String prefix = Option.GLOBAL_CHAT_PREFIX;


        if (Option.GLOBAL_CHAT_ENABLED && message.length() > prefix.length() && message.substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
            event.setFormat(Util.color(Option.GLOBAL_CHAT_FORMAT
                    .replace("%player%", "%s")
                    .replace("%message%", "%s")));
            event.setMessage(message.replaceFirst(prefix, ""));
            return;
        }
        event.getRecipients().clear();
        event.getRecipients().addAll(event.getPlayer().getWorld().getPlayers());
    }
}