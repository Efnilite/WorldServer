package dev.efnilite.ws;

import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.vilib.util.Strings;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.ws.config.Config;
import dev.efnilite.ws.config.Option;
import dev.efnilite.ws.eco.BalCache;
import dev.efnilite.ws.hook.PlaceholderHook;
import dev.efnilite.ws.menu.EcoTopMenu;
import dev.efnilite.ws.menu.WorldServerMenu;
import dev.efnilite.ws.util.GroupUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static dev.efnilite.ws.eco.EconomyProvider.CURRENCY_FORMAT;

public class WorldServerCommand extends ViCommand {

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(Strings.colour(sender instanceof Player ? PlaceholderHook.translate((Player) sender, message) : message));
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0 -> {
                if (Option.WS_REQUIRES_PERMISSION && !sender.hasPermission("ws")) {
                    send(sender, "<red>You don't have enough permissions to do that.");
                    return true;
                }

                send(sender, "");
                send(sender, "<dark_gray><strikethrough>-----------<reset> " + WorldServer.NAME + " <dark_gray><strikethrough>-----------");
                send(sender, "");
                send(sender, "<gray>/ws <dark_gray>- The main command");
                send(sender, "<gray>/ws global or /ws gc <dark_gray>- Toggle global chat");
                if (sender.hasPermission("ws.menu")) {
                    send(sender, "<gray>/ws menu <dark_gray>- Change all settings quickly");
                }
                if (sender.hasPermission("ws.eco.bal") && Option.ECONOMY_ENABLED) {
                    send(sender, "<gray>/ws bal [player] <dark_gray>- View a player's balance. Player optional.");
                }
                if (sender.hasPermission("ws.eco.pay") && Option.ECONOMY_ENABLED) {
                    send(sender, "<gray>/ws pay <player> <dark_gray>- Pay another player.");
                }
                if (sender.hasPermission("ws.eco.admin.edit") && Option.ECONOMY_ENABLED) {
                    send(sender, "<gray>/ws eco <set|add|remove> <player> <amount> [world/group] <dark_gray>- Edit player balances. World/group optional.");
                }
                if (sender.hasPermission("ws.reload")) {
                    send(sender, "<gray>/ws reload <dark_gray>- Reload the config and commands");
                }
                if (sender.isOp()) {
                    send(sender, "<gray><bold>/ws permissions<dark_gray>- Get all permissions");
                }
                send(sender, "");
                send(sender, "<dark_gray><strikethrough>---------------------------------");
                send(sender, "");
                return true;
            }
            case 1 -> {
                switch (args[0].toLowerCase()) {
                    case "reload" -> {
                        if (!sender.hasPermission("ws.reload")) {
                            send(sender, "<red>You don't have enough permissions to do that.");
                            return true;
                        }
                        Time.timerStart("reload");
                        Config.reload();
                        send(sender, WorldServer.MESSAGE_PREFIX + "Reloaded WorldServer in " + Time.timerEnd("reload") + "ms!");
                        return true;
                    }
                    case "menu" -> {
                        if (!(sender instanceof Player) || !sender.hasPermission("ws.menu")) {
                            send(sender, "<red>You don't have enough permissions to do that.");
                            return true;
                        }
                        WorldServerMenu.openMainMenu((Player) sender);
                        return true;
                    }
                    case "permissions" -> {
                        send(sender, "");
                        send(sender, "<dark_gray><strikethrough>-----------<reset> <gradient:#3D626F:#0EACE2>Permissions<reset> <dark_gray><strikethrough>-----------");
                        send(sender, "");
                        send(sender, "<gray>ws.reload <dark_gray>- Reloads the config");
                        send(sender, "<gray>ws.menu <dark_gray>- For opening and viewing the menu");
                        send(sender, "<gray>ws.spy <dark_gray>- For spying on what everyone in every world is saying. This requires the ws.menu permission.");
                        send(sender, "<gray>ws.option.global-chat <dark_gray>- For changing global chat settings");
                        send(sender, "<gray>ws.option.chat <dark_gray>- For changing chat settings");
                        send(sender, "<gray>ws.chat.cooldown.bypass <dark_gray>- For bypassing the chat cooldowns");
                        send(sender, "<gray>ws.option.tab <dark_gray>- For changing tab settings");
                        send(sender, "<gray>ws.eco.bal <dark_gray>- For using the /ws bal, /bal or /balance command");
                        send(sender, "<gray>ws.eco.pay <dark_gray>- For using the /ws pay or /pay command");
                        send(sender, "<gray>ws.eco.top <dark_gray>- For using the /ws baltop, /baltop or /balancetop command");
                        send(sender, "<gray>ws.eco.admin <dark_gray>- For using the /ws eco command");
                        send(sender, "");
                        send(sender, "<dark_gray><strikethrough>---------------------------------");
                        send(sender, "");
                        return true;
                    }
                    case "bal", "balance" -> {
                        if (!sender.hasPermission("ws.eco.bal") || !Option.ECONOMY_ENABLED || !(sender instanceof Player p)) {
                            send(sender, "<red>You don't have enough permissions to do that.");
                            return true;
                        }
                        WorldPlayer player = WorldPlayer.getPlayer(p);
                        player.send(Option.ECONOMY_SWITCH_FORMAT.replace("%amount%", CURRENCY_FORMAT.format(player.getBalance())));
                        return true;
                    }
                    case "top", "baltop" -> {
                        if (!sender.hasPermission("ws.eco.top") || !Option.ECONOMY_ENABLED) {
                            send(sender, "<red>You don't have enough permissions to do that.");
                            return true;
                        }
                        Task.create(WorldServer.getPlugin()).execute(() -> EcoTopMenu.open(WorldPlayer.getPlayer((Player) sender))).run();
                        return true;
                    }
                    case "global", "gc" -> {
                        if (!(sender instanceof Player player)) {
                            return true;
                        }

                        var wp = WorldPlayer.getPlayer(player);
                        var globalChat = wp.isGlobalChat();

                        if (globalChat) {
                            wp.send(Config.CONFIG.getString("global-chat-change.on"));
                        } else {
                            wp.send(Config.CONFIG.getString("global-chat-change.off"));
                        }

                        wp.setGlobalChat(!globalChat);
                        return true;
                    }
                }
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "bal", "balance" -> {
                        if (!sender.hasPermission("ws.eco.bal") || !Option.ECONOMY_ENABLED) {
                            send(sender, "<red>You don't have enough permissions to do that.");
                            return true;
                        }
                        Player of = Bukkit.getPlayer(args[1]);

                        if (of == null) {
                            send(sender, "<red>Couldn't find that player!");
                            return true;
                        }

                        send(sender, Option.ECONOMY_BALANCE_FORMAT
                                .replace("%player%", of.getName())
                                .replace("%amount%", Double.toString(WorldPlayer.getPlayer(of).getBalance())));
                        return true;
                    }
                }
                return true;
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("pay") && Option.ECONOMY_ENABLED) {
                    if (!sender.hasPermission("ws.eco.pay")) {
                        send(sender, "<red>You don't have enough permissions to do that.");
                        return true;
                    }

                    Player p = Bukkit.getPlayerExact(args[1]);

                    if (p == null) {
                        send(sender, "<red>Couldn't find that player!");
                        return true;
                    }

                    double amount;
                    try {
                        amount = Double.parseDouble(args[2]);
                    } catch (NumberFormatException ex) {
                        send(sender, "<red>That isn't a valid number!");
                        return true;
                    }

                    WorldPlayer to = WorldPlayer.getPlayer(p);
                    String group = to.getWorldGroup();

                    if (GroupUtil.getWorlds(group).isEmpty()) {
                        send(sender, "<red>Couldn't find that world or group!");
                        return true;
                    }

                    if (!(sender instanceof Player)) {
                        send(sender, "<red>Only players can execute this command");
                        return true;
                    }
                    WorldPlayer from = WorldPlayer.getPlayer(((Player) sender));

                    if (from.getBalance(group) < amount) {
                        from.send(Option.ECONOMY_PAY_NO_FUNDS_FORMAT);
                        return true;
                    }

                    from.withdraw(amount); // withdraw from sender
                    to.deposit(amount);

                    from.send(Option.ECONOMY_PAY_SEND_FORMAT.replace("%player%", to.player.getName())
                            .replace("%amount%", Double.toString(amount)));
                    to.send(Option.ECONOMY_PAY_RECEIVE_FORMAT.replace("%player%", from.player.getName())
                            .replace("%amount%", Double.toString(amount)));
                    return true;
                }
                return true;
            }
            case 4, 5 -> {
                if (args[0].equalsIgnoreCase("eco") && Option.ECONOMY_ENABLED) {
                    if (!sender.hasPermission("ws.eco.admin")) {
                        send(sender, "<red>You don't have enough permissions to do that.");
                        return true;
                    }

                    switch (args[1].toLowerCase()) {
                        case "reset" -> {
                            String player = args[2];
                            String group = args[3];

                            if (Option.GROUPS.containsKey(group.toLowerCase())) {
                                send(sender, "<red>Couldn't find that world or group!");
                                return true;
                            }

                            double defaultAmount = Option.ECONOMY_STARTING_AMOUNT.getOrDefault(group, 1000.0);

                            if (player.equalsIgnoreCase("everyone")) {
                                for (UUID uuid : BalCache.getUUIDs()) {
                                    Map<String, Double> currentBalances = BalCache.BALANCES.get(uuid);

                                    if (currentBalances.containsKey(group)) {
                                        Player onlinePlayer = Bukkit.getPlayer(uuid);

                                        if (onlinePlayer != null) {
                                            WorldPlayer worldPlayer = WorldPlayer.getPlayer(onlinePlayer);

                                            // update online if player is online
                                            worldPlayer.setBalance(defaultAmount, group);
                                        } else {
                                            // update in store if player is not online
                                            BalCache.saveAll(uuid, group, defaultAmount);
                                        }
                                    }
                                }

                                BalCache.saveAll();

                                send(sender, "All players' balances in group %s have been reset to %s".formatted(group, defaultAmount));
                                return true;
                            }

                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
                            UUID uuid = offlinePlayer.getUniqueId();

                            if (!BalCache.BALANCES.containsKey(uuid)) {
                                send(sender, "<red>Couldn't find that player!");
                                return true;
                            }

                            Player onlinePlayer = Bukkit.getPlayer(uuid);

                            if (onlinePlayer != null) {
                                WorldPlayer worldPlayer = WorldPlayer.getPlayer(onlinePlayer);

                                // update online if player is online
                                worldPlayer.setBalance(defaultAmount, group);
                            } else {
                                // update in store if player is not online
                                BalCache.saveAll(uuid, group, defaultAmount);
                            }

                            send(sender, "Set %s's balance to %s".formatted(offlinePlayer.getName(), defaultAmount));
                        }

                        case "set", "add", "remove" -> {
                            Player p = Bukkit.getPlayerExact(args[2]);

                            if (p == null) {
                                send(sender, "<red>Couldn't find that player!");
                                return true;
                            }

                            double amount;
                            try {
                                amount = Double.parseDouble(args[3]);
                            } catch (NumberFormatException ex) {
                                send(sender, "<red>That isn't a valid number!");
                                return true;
                            }

                            WorldPlayer to = WorldPlayer.getPlayer(p);
                            String group = to.getWorldGroup();

                            if (args.length == 5) {
                                group = args[4];
                            }

                            if (GroupUtil.getWorlds(group).isEmpty()) {
                                send(sender, "<red>Couldn't find that world or group!");
                                return true;
                            }

                            switch (args[1].toLowerCase()) {
                                case "set" -> {
                                    to.setBalance(amount, group);
                                    send(sender, "%sSuccessfully set %s's balance to %s".formatted(WorldServer.MESSAGE_PREFIX, to.player.getName(), amount));
                                }
                                case "add" -> {
                                    to.deposit(group, amount);
                                    send(sender, "%sSuccessfully added %s to %s's balance".formatted(WorldServer.MESSAGE_PREFIX, amount, to.player.getName()));
                                    if (Option.ECONOMY_BALANCE_CHANGE) {
                                        send(to.player, Option.ECONOMY_BALANCE_CHANGE_FORMAT.replace("%amount%", CURRENCY_FORMAT.format(amount)).replace("%prefix%", "+"));
                                    }
                                }
                                case "remove" -> {
                                    to.withdraw(group, amount);
                                    send(sender, "%sSuccessfully removed %s from %s's balance".formatted(WorldServer.MESSAGE_PREFIX, amount, to.player.getName()));
                                    if (Option.ECONOMY_BALANCE_CHANGE) {
                                        send(to.player, Option.ECONOMY_BALANCE_CHANGE_FORMAT.replace("%amount%", CURRENCY_FORMAT.format(amount)).replace("%prefix%", "-"));
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("ws.menu")) {
                completions.add("menu");
            }
            if (sender.hasPermission("ws.eco.bal") && Option.ECONOMY_ENABLED) {
                completions.add("bal");
            }
            if (sender.hasPermission("ws.eco.top") && Option.ECONOMY_ENABLED) {
                completions.add("top");
            }
            if (sender.hasPermission("ws.eco.pay") && Option.ECONOMY_ENABLED) {
                completions.add("pay");
            }
            if (sender.hasPermission("ws.eco.admin") && Option.ECONOMY_ENABLED) {
                completions.add("eco");
            }
            if (sender.hasPermission("ws.reload")) {
                completions.add("reload");
            }
            if (sender.isOp()) {
                completions.add("permissions");
            }
            return completions(args[0], completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("eco")) {
                if (sender.hasPermission("ws.eco.admin") && Option.ECONOMY_ENABLED) {
                    completions.add("reset");
                    completions.add("set");
                    completions.add("add");
                    completions.add("remove");
                }
            }
            return completions(args[1], completions);
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("eco")) {
                if (args[1].equalsIgnoreCase("reset")) {
                    completions.add("everyone");
                }

                if (sender.hasPermission("ws.eco.admin") && Option.ECONOMY_ENABLED) {
                    completions.addAll(Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .toList());
                }
            }
            return completions(args[2], completions);
        } else {
            return Collections.emptyList();
        }
    }
}