package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.hook.PlaceholderHook;
import dev.efnilite.worldserver.hook.VaultHook;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldChatListener extends Toggleable implements EventWatcher {

    protected Map<String, Map<UUID, Long>> lastExecuted = new HashMap<>();

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();

        String fromGroup = Option.getGroupFromWorld(from);
        String toGroup = Option.getGroupFromWorld(to);

        String fromMessage = Option.CHAT_JOIN_FORMATS.get(fromGroup);
        String toMessage = Option.CHAT_LEAVE_FORMATS.get(toGroup);

        if (fromMessage == null || toMessage == null) {
            return;
        }

        for (Player pl : getPlayersInWorldGroup(from)) {
            Message.send(pl, fromMessage.replace("%player%",
                    Option.CHAT_AFFIXES ? VaultHook.getPrefix(player) + player.getName() + VaultHook.getSuffix(player) : player.getName()));
        }
        for (Player pl : getPlayersInWorldGroup(to)) {
            Message.send(pl, toMessage.replace("%player%",
                    Option.CHAT_AFFIXES ? VaultHook.getPrefix(player) + player.getName() + VaultHook.getSuffix(player) : player.getName()));
        }
    } // todo add joining/leaving support

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

            cooldown(event, "global");
            blocked(event);
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

        cooldown(event, group);
        blocked(event);
    }

    // checks if this message should by cancelled by the cooldown
    private void cooldown(AsyncPlayerChatEvent event, String group) {
        Player player = event.getPlayer();
        if (player.hasPermission("ws.chat.cooldown.bypass")) {
            return;
        }
        UUID uuid = player.getUniqueId();

        double cooldown = Option.CHAT_COOLDOWN.getOrDefault(group, 0D);
        Map<UUID, Long> map = lastExecuted.getOrDefault(group, new HashMap<>());

        long remaining = System.currentTimeMillis() - map.getOrDefault(uuid, 0L);
        if (remaining < cooldown * 1000) { // if cooldown is longer than last time, cancel message
            event.setCancelled(true);

            Util.send(player, Option.CHAT_COOLDOWN_FORMAT
                    .replace("%remaining%", String.format("%.2f", cooldown - (remaining / 1000D)))
                    .replace("%time%", String.format("%.2f", cooldown)));
        } else {
            map.put(uuid, System.currentTimeMillis());
            lastExecuted.put(group, map);
        }
    }

    // check blocked messages
    private void blocked(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        for (String blocked : Option.CHAT_BLOCKED) {
            if (message.toLowerCase().contains(blocked.toLowerCase())) {
                event.setCancelled(true);
                Util.send(event.getPlayer(), Option.CHAT_BLOCKED_FORMAT);
                return;
            }
        }
    }

    private String getChatFormatted(Player player, String format) {
        return Message.parseFormatting(PlaceholderHook.translate(player, format)
                .replace("%player%", Option.CHAT_AFFIXES ? VaultHook.getPrefix(player) + "%s" + VaultHook.getSuffix(player) : "%s")
                .replace("%message%", "%s"));
    }
}