package dev.efnilite.worldserver.group;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.vilib.util.Strings;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.config.Option;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WorldChatListener implements EventWatcher {

    protected final Map<String, Map<UUID, Long>> lastExecuted = new HashMap<>();

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!Option.CHAT_ENABLED) {
            return;
        }
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();

        String fromGroup = GroupUtil.getGroupFromWorld(from);
        String toGroup = GroupUtil.getGroupFromWorld(to);

        String fromMessage = getMessage(from, Option.CHAT_LEAVE_FORMATS);
        String toMessage = getMessage(to, Option.CHAT_JOIN_FORMATS);

        if (fromGroup.equals(toGroup)) {
            return;
        }

        if (fromMessage != null) {
            for (Player pl : GroupUtil.getPlayersInWorldGroup(from)) { // from send leave
                pl.sendMessage(getColouredMessage(player, fromMessage).replace("%player%", player.getName()));
            }
        }

        if (Option.CLEAR_CHAT_ON_SWITCH) {
            for (int i = 0; i < 100; i++) {
                Util.send(player, "");
            }
        }

        if (toMessage != null) {
            for (Player pl : GroupUtil.getPlayersInWorldGroup(to)) {
                pl.sendMessage(getColouredMessage(player, toMessage).replace("%player%", player.getName()));
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        if (!Option.CHAT_ENABLED) {
            return;
        }
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        World initialWorld = player.getWorld();

        Task.create(WorldServer.getPlugin()).delay(1).execute(() -> {
            if (initialWorld != player.getWorld()) {
                return;
            }

            performNetworkMessage(player, Option.CHAT_JOIN_FORMATS);
        }).run();
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        if (!Option.CHAT_ENABLED) {
            return;
        }
        event.setQuitMessage(null);

        performNetworkMessage(event.getPlayer(), Option.CHAT_LEAVE_FORMATS);
    }

    private void performNetworkMessage(Player player, Map<String, String> formats) {
        World world = player.getWorld();
        String message = getMessage(world, formats);

        if (message == null) {
            return;
        }

        for (Player pl : GroupUtil.getPlayersInWorldGroup(world)) {
            pl.sendMessage(getColouredMessage(player, message).replace("%player%", player.getName()));
        }
    }

    @Nullable
    private String getMessage(World world, Map<String, String> formats) {
        String group = GroupUtil.getGroupFromWorld(world);
        String message = formats.get(group);

        return message == null ? formats.get(world.getName()) : message;
    }

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
            event.setFormat(getFormattedMessage(player, Option.GLOBAL_CHAT_FORMAT));
            event.setMessage(message.replaceFirst(prefix, ""));

            cooldown(event, "global");
            blocked(event);
            return;
        }

        World world = player.getWorld();

        // Update recipients with world groups
        List<Player> sendTo = GroupUtil.getPlayersInWorldGroup(world);

        event.getRecipients().clear();
        event.getRecipients().addAll(sendTo);

        String spy = Option.SPY_FORMAT
                .replace("%world%", world.getName())
                .replace("%player%", player.getName())
                .replace("%message%", event.getMessage());

        for (WorldPlayer wp : WorldPlayer.PLAYERS.values()) {
            if (wp.spyMode && !sendTo.contains(wp.player)) {
                wp.send(spy);
            }
        }

        // Update possible formatting for groups and single worlds
        String format = Option.CHAT_FORMAT.get(world.getName());
        String group = GroupUtil.getGroupFromWorld(world);

        // prioritize worlds over groups
        if (format == null) {
            format = Option.CHAT_FORMAT.get(group);
        }

        if (format != null) {
            String formatted = getFormattedMessage(player, format);

            try {
                event.setFormat(formatted);
                // unknown placeholder
            } catch (UnknownFormatConversionException ex) {
                WorldServer.logging().stack("Unknown placeholder in message: " + formatted, "check if you spelled the placeholders correctly", ex);
            }
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

        double cooldown = Option.CHAT_COOLDOWN.getOrDefault(group, 0D);
        Map<UUID, Long> map = lastExecuted.getOrDefault(group, new HashMap<>());

        long remaining = System.currentTimeMillis() - map.getOrDefault(uuid, 0L);
        if (remaining < cooldown * 1000) { // if cooldown is longer than last time, cancel message
            event.setCancelled(true);

            Util.send(player, Option.CHAT_COOLDOWN_FORMAT.replace("%remaining%",
                    String.format("%.2f", cooldown - (remaining / 1000.0)))
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

    private String getColouredMessage(Player player, String message) {
        return Util.colorLegacy(Strings.colour(PlaceholderHook.translate(player, message
                .replace("%prefix%", VaultHook.getPrefix(player))
                .replace("%suffix%", VaultHook.getSuffix(player)))));
    }

    private String getFormattedMessage(Player player, String message) {
        return getColouredMessage(player, message)
                .replace("%world%", player.getWorld().getName())
                .replace("%player%", "%s")
                .replace("%message%", "%s"); // color everything except for messages
    }
}