package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.vilib.util.Strings;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.ConfigValue;
import dev.efnilite.worldserver.hook.PlaceholderHook;
import dev.efnilite.worldserver.hook.VaultHook;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldChatListener extends Toggleable implements EventWatcher {

    protected final Map<String, Map<UUID, Long>> lastExecuted = new HashMap<>();

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!ConfigValue.CHAT_ENABLED) {
            return;
        }
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();

        String fromGroup = ConfigValue.getGroupFromWorld(from);
        String toGroup = ConfigValue.getGroupFromWorld(to);

        String fromMessage = getMessage(from, ConfigValue.CHAT_LEAVE_FORMATS);
        String toMessage = getMessage(to, ConfigValue.CHAT_JOIN_FORMATS);

        if (fromGroup.equals(toGroup)) {
            return;
        }

        if (fromMessage != null) {
            for (Player pl : getPlayersInWorldGroup(from)) { // from send leave
                pl.sendMessage(getColouredMessage(player, fromMessage).replace("%player%", player.getName()));
            }
        }

        if (ConfigValue.CLEAR_CHAT_ON_SWITCH) {
            for (int i = 0; i < 100; i++) {
                Util.send(player, "");
            }
        }

        if (toMessage != null) {
            for (Player pl : getPlayersInWorldGroup(to)) {
                pl.sendMessage(getColouredMessage(player, toMessage).replace("%player%", player.getName()));
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        if (!ConfigValue.CHAT_ENABLED) {
            return;
        }
        event.setJoinMessage(null);

        performNetworkMessage(event.getPlayer(), ConfigValue.CHAT_JOIN_FORMATS);
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        if (!ConfigValue.CHAT_ENABLED) {
            return;
        }
        event.setQuitMessage(null);

        performNetworkMessage(event.getPlayer(), ConfigValue.CHAT_LEAVE_FORMATS);
    }

    private void performNetworkMessage(Player player, Map<String, String> formats) {
        World world = player.getWorld();
        String message = getMessage(world, formats);

        if (message == null) {
            return;
        }

        for (Player pl : getPlayersInWorldGroup(world)) {
            pl.sendMessage(getColouredMessage(player, message).replace("%player%", player.getName()));
        }
    }

    @Nullable
    private String getMessage(World world, Map<String, String> formats) {
        String group = ConfigValue.getGroupFromWorld(world);
        String message = formats.get(group);

        return message == null ? formats.get(world.getName()) : message;
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
            event.setFormat(getFormattedMessage(player, ConfigValue.GLOBAL_CHAT_FORMAT));
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
            event.setFormat(getFormattedMessage(player, format));
        }

        cooldown(event, group);
        blocked(event);
    }

    // checks if this message should be cancelled by the cooldown
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

            Util.send(player, ConfigValue.CHAT_COOLDOWN_FORMAT.replace("%remaining%", String.format("%.2f", cooldown - (remaining / 1000D))).replace("%time%", String.format("%.2f", cooldown)));
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

    private String getColouredMessage(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', Strings.colour(PlaceholderHook.translate(player, message)));

        return message.replace("%player%", ConfigValue.CHAT_AFFIXES ? (VaultHook.getPrefix(player) + " %player% " + VaultHook.getSuffix(player)).trim() : "%player%");
    }

    private String getFormattedMessage(Player player, String message) {
        return getColouredMessage(player, message).replace("%world%", player.getWorld().getName()).replace("%player%", "%s").replace("%message%", "%s"); // color everything except for messages
    }
}