package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.ConfigValue;
import dev.efnilite.worldserver.hook.PlaceholderHook;
import dev.efnilite.worldserver.hook.VaultHook;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldChatListener extends Toggleable implements EventWatcher {

    protected final Map<String, Map<UUID, Long>> lastExecuted = new HashMap<>();

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();

        String fromGroup = ConfigValue.getGroupFromWorld(from);
        String toGroup = ConfigValue.getGroupFromWorld(to);

        String fromMessage = ConfigValue.CHAT_JOIN_FORMATS.get(fromGroup);
        String toMessage = ConfigValue.CHAT_LEAVE_FORMATS.get(toGroup);

        if (fromMessage == null || toMessage == null) {
            return;
        }

        for (Player pl : getPlayersInWorldGroup(from)) {
            Message.send(pl, fromMessage.replace("%player%",
                    ConfigValue.CHAT_AFFIXES ? VaultHook.getPrefix(player) + player.getName() + VaultHook.getSuffix(player) : player.getName()));
        }
        for (Player pl : getPlayersInWorldGroup(to)) {
            Message.send(pl, toMessage.replace("%player%",
                    ConfigValue.CHAT_AFFIXES ? VaultHook.getPrefix(player) + player.getName() + VaultHook.getSuffix(player) : player.getName()));
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        String group = ConfigValue.getGroupFromWorld(world);

        String message = ConfigValue.CHAT_JOIN_FORMATS.get(group);

        if (message == null) {
            return;
        }
        event.setJoinMessage(null);

        for (Player pl : getPlayersInWorldGroup(world)) {
            Message.send(pl, message.replace("%player%",
                    ConfigValue.CHAT_AFFIXES ? VaultHook.getPrefix(player) + player.getName() + VaultHook.getSuffix(player) : player.getName()));
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        String group = ConfigValue.getGroupFromWorld(world);

        String message = ConfigValue.CHAT_LEAVE_FORMATS.get(group);

        if (message == null) {
            return;
        }
        event.setQuitMessage(null);

        for (Player pl : getPlayersInWorldGroup(world)) {
            Message.send(pl, message.replace("%player%",
                    ConfigValue.CHAT_AFFIXES ? VaultHook.getPrefix(player) + player.getName() + VaultHook.getSuffix(player) : player.getName()));
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        if (!ConfigValue.CHAT_ENABLED) {
            return;
        }
        Player player = event.getPlayer();
        String message = event.getMessage();
        String prefix = ConfigValue.GLOBAL_CHAT_PREFIX;

        // Global chat handling
        if (ConfigValue.GLOBAL_CHAT_ENABLED && message.length() > prefix.length() && message.substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
            event.setFormat(getChatFormatted(player, ConfigValue.GLOBAL_CHAT_FORMAT));
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

        String spy = ConfigValue.SPY_FORMAT
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
        String group = ConfigValue.getGroupFromWorld(world);
        if (group.equals("")) {
            format = ConfigValue.CHAT_FORMAT.get(world.getName());
        } else {
            format = ConfigValue.CHAT_FORMAT.get(group);
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

        double cooldown = ConfigValue.CHAT_COOLDOWN.getOrDefault(group, 0D);
        Map<UUID, Long> map = lastExecuted.getOrDefault(group, new HashMap<>());

        long remaining = System.currentTimeMillis() - map.getOrDefault(uuid, 0L);
        if (remaining < cooldown * 1000) { // if cooldown is longer than last time, cancel message
            event.setCancelled(true);

            Util.send(player, ConfigValue.CHAT_COOLDOWN_FORMAT
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
        for (String blocked : ConfigValue.CHAT_BLOCKED) {
            if (message.toLowerCase().contains(blocked.toLowerCase())) {
                event.setCancelled(true);
                Util.send(event.getPlayer(), ConfigValue.CHAT_BLOCKED_FORMAT);
                return;
            }
        }
    }

    private String getChatFormatted(Player player, String format) {
        return Message.parseFormatting(PlaceholderHook.translate(player, format)
                .replace("%player%", ConfigValue.CHAT_AFFIXES ? VaultHook.getPrefix(player) + "%s" + VaultHook.getSuffix(player) : "%s")
                .replace("%message%", "%s"));
    }
}