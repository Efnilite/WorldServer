package dev.efnilite.worldserver;

import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.menu.EcoTopMenu;
import dev.efnilite.worldserver.menu.WorldServerMenu;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldServerCommand extends ViCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0:
                Message.send(sender, "");
                Message.send(sender, "<dark_gray><strikethrough>-----------&r " + WorldServer.NAME + " <dark_gray><strikethrough>-----------");
                Message.send(sender, "");
                Message.send(sender, "<gray>/ws <dark_gray>- The main command");
                Message.send(sender, "<gray>/ws permissions<dark_gray>- Get all permissions");
                if (sender.hasPermission("ws.menu")) {
                    Message.send(sender, "<gray>/ws menu <dark_gray>- Change all settings quickly");
                }
                if (sender.hasPermission("ws.eco.bal") && Option.ECONOMY_ENABLED) {
                    Message.send(sender, "<gray>/ws bal [player] <dark_gray>- View a player's balance. Player optional.");
                }
                if (sender.hasPermission("ws.eco.pay") && Option.ECONOMY_ENABLED) {
                    Message.send(sender, "<gray>/ws pay <player> <dark_gray>- Pay another player.");
                }
                if (sender.hasPermission("ws.eco.admin.edit") && Option.ECONOMY_ENABLED) {
                    Message.send(sender, "<gray>/ws eco <set|add|remove> <player> <amount> [world/group] <dark_gray>- Edit player balances. World/group optional.");
                }
//                if (sender.hasPermission("ws.eco.admin.transfer") && Option.ECONOMY_ENABLED) {
//                    Message.send(sender, "<gray>/ws eco transfer <player> <world 1/group 1> <world 2/group 2><dark_gray>- Transfer funds between worlds/groups.");
//                } todo
                if (sender.hasPermission("ws.reload")) {
                    Message.send(sender, "<gray>/ws reload <dark_gray>- Reload the config and commands");
                }
                Message.send(sender, "");
                Message.send(sender, "<dark_gray><strikethrough>---------------------------------");
                Message.send(sender, "");
                return true;
            case 1:
                switch (args[0].toLowerCase()) {
                    case "reload":
                        if (!sender.hasPermission("ws.reload")) {
                            return true;
                        }
                        Time.timerStart("reload");
                        WorldServer.getConfiguration().reload();
                        Message.send(sender, WorldServer.MESSAGE_PREFIX + "Reloaded WorldServer in " + Time.timerEnd("reload") + "ms!");
                        return true;
                    case "menu":
                        if (sender instanceof Player && sender.hasPermission("ws.menu")) {
                            WorldServerMenu.openMainMenu((Player) sender);
                        }
                        return true;
                    case "permissions":
                        Message.send(sender, "");
                        Message.send(sender, "<dark_gray><strikethrough>-----------&r <gradient:#3D626F>Permissions</gradient:#0EACE2> <dark_gray><strikethrough>-----------");
                        Message.send(sender, "");
                        Message.send(sender, "<gray>ws.reload <dark_gray>- Reloads the config");
                        Message.send(sender, "<gray>ws.menu <dark_gray>- For opening and viewing the menu");
                        Message.send(sender, "<gray>ws.spy <dark_gray>- For spying on what everyone in every world is saying. This requires the ws.menu permission.");
                        Message.send(sender, "<gray>ws.option.global-chat <dark_gray>- For changing global chat settings");
                        Message.send(sender, "<gray>ws.option.chat <dark_gray>- For changing chat settings");
                        Message.send(sender, "<gray>ws.chat.cooldown.bypass <dark_gray>- For bypassing the chat cooldowns");
                        Message.send(sender, "<gray>ws.option.tab <dark_gray>- For changing tab settings");
                        Message.send(sender, "<gray>ws.eco.bal <dark_gray>- For using the /ws bal, /bal or /balance command");
                        Message.send(sender, "<gray>ws.eco.pay <dark_gray>- For using the /ws pay or /pay command");
                        Message.send(sender, "<gray>ws.eco.top <dark_gray>- For using the /ws baltop, /baltop or /balancetop command");
                        Message.send(sender, "<gray>ws.eco.admin <dark_gray>- For using the /ws eco command");
                        Message.send(sender, "");
                        Message.send(sender, "<dark_gray><strikethrough>---------------------------------");
                        Message.send(sender, "");
                        return true;
                    case "bal":
                    case "balance":
                        if (sender.hasPermission("ws.eco.bal") && Option.ECONOMY_ENABLED && sender instanceof Player) {
                            Player p = (Player) sender;
                            WorldPlayer player = WorldPlayer.getPlayer(p);
                            player.send(Option.ECONOMY_SWITCH_FORMAT.replace("%amount%", Util.CURRENCY_FORMAT.format(player.getBalance())));
                        }
                        return true;
                    case "top":
                    case "baltop":
                        if (sender.hasPermission("ws.eco.top") && Option.ECONOMY_ENABLED) {
                            EcoTopMenu.open(WorldPlayer.getPlayer((Player) sender));
                        }
                        return true;
                }
                break;
            case 2:
                switch (args[0].toLowerCase()) {
                    case "bal":
                    case "balance":
                        if (sender.hasPermission("ws.eco.bal") && Option.ECONOMY_ENABLED) {
                            Player of = Bukkit.getPlayer(args[1]);

                            if (of == null) {
                                Message.send(sender, WorldServer.MESSAGE_PREFIX + "Couldn't find that player!");
                                return true;
                            }

                            Message.send(sender, Option.ECONOMY_BALANCE_FORMAT
                                    .replace("%player%", of.getName())
                                    .replace("%amount%", Double.toString(WorldPlayer.getPlayer(of).getBalance())));
                        }
                        return true;
                }
                return true;
            case 3:
                if (args[0].equalsIgnoreCase("pay") && sender.hasPermission("ws.eco.pay") && Option.ECONOMY_ENABLED) {
                    Player p = Bukkit.getPlayerExact(args[1]);

                    if (p == null) {
                        Message.send(sender, WorldServer.MESSAGE_PREFIX + "Couldn't find that player!");
                        return true;
                    }

                    double amount;
                    try {
                        amount = Double.parseDouble(args[2]);
                    } catch (NumberFormatException ex) {
                        Message.send(sender, WorldServer.MESSAGE_PREFIX + "That isn't a valid number!");
                        return true;
                    }

                    WorldPlayer to = WorldPlayer.getPlayer(p);
                    String group = to.getWorldGroup();

                    if (Option.getWorlds(group).isEmpty()) {
                        Message.send(sender, WorldServer.MESSAGE_PREFIX + "Couldn't find that world or group!");
                        return true;
                    }

                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    WorldPlayer from = WorldPlayer.getPlayer(((Player) sender));

                    if (from.getBalance(group) < amount) {
                        from.send(Option.ECONOMY_PAY_NO_FUNDS_FORMAT);
                        return true;
                    }

                    from.withdraw(amount); // withdraw from sender
                    to.deposit(amount);

                    from.send(Option.ECONOMY_PAY_SEND_FORMAT.replace("%player%", to.getPlayer().getName()).replace("%amount%", Double.toString(amount)));
                    to.send(Option.ECONOMY_PAY_RECEIVE_FORMAT.replace("%player%", from.getPlayer().getName()).replace("%amount%", Double.toString(amount)));
                    return true;
                }
                return true;
            case 4:
            case 5:
                if (args[0].equalsIgnoreCase("eco") && sender.hasPermission("ws.eco.admin") && Option.ECONOMY_ENABLED) {
                    Player p = Bukkit.getPlayerExact(args[2]);

                    if (p == null) {
                        Message.send(sender, WorldServer.MESSAGE_PREFIX + "Couldn't find that player!");
                        return true;
                    }

                    double amount;
                    try {
                        amount = Double.parseDouble(args[3]);
                    } catch (NumberFormatException ex) {
                        Message.send(sender, WorldServer.MESSAGE_PREFIX + "That isn't a valid number!");
                        return true;
                    }

                    WorldPlayer to = WorldPlayer.getPlayer(p);
                    String group = to.getWorldGroup();

                    if (args.length == 5) {
                        group = args[4];
                    }

                    if (Option.getWorlds(group).isEmpty()) {
                        Message.send(sender, WorldServer.MESSAGE_PREFIX + "Couldn't find that world or group!");
                        return true;
                    }

                    switch (args[1].toLowerCase()) {
                        case "set":
                            to.setBalance(amount, group);
                            Message.send(sender, WorldServer.MESSAGE_PREFIX +
                                    "Successfully set " + to.getPlayer().getName() + "'s balance to " + amount);
                            return true;
                        case "add":
                            to.deposit(group, amount);
                            Message.send(sender, WorldServer.MESSAGE_PREFIX +
                                    "Successfully added " + amount + " to " + to.getPlayer().getName() + "'s balance");
                            return true;
                        case "remove":
                            to.withdraw(group, amount);
                            Message.send(sender, WorldServer.MESSAGE_PREFIX +
                                    "Successfully removed " + amount + " from " + to.getPlayer().getName() + "'s balance");
                            return true;
                    }
                }
                return true;
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("permissions");
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
            return completions(args[0], completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("eco")) {
                if (sender.hasPermission("ws.eco.admin") && Option.ECONOMY_ENABLED) {
                    completions.add("set");
                    completions.add("add");
                    completions.add("remove");
                }
            }
            return completions(args[1], completions);
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("eco")) {
                if (sender.hasPermission("ws.eco.admin") && Option.ECONOMY_ENABLED) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                }
            }
            return completions(args[2], completions);
        } else {
            return Collections.emptyList();
        }
    }
}