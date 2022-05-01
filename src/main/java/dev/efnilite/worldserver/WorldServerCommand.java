package dev.efnilite.worldserver;

import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.worldserver.config.Option;
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
                if (sender.hasPermission("ws.eco") && Option.ECONOMY_ENABLED) {
                    Message.send(sender, "<gray>/ws eco <set|add|remove> <player> <amount> <dark_gray>- Admin commands for setting player balances");
                }
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
                        Message.send(sender, "<gray>ws.option.tab <dark_gray>- For changing tab settings");
                        Message.send(sender, "<gray>ws.bal <dark_gray>- For using the /ws bal, /bal or /balance command");
                        Message.send(sender, "<gray>ws.eco <dark_gray>- For using the /ws eco command");
                        Message.send(sender, "");
                        Message.send(sender, "<dark_gray><strikethrough>---------------------------------");
                        Message.send(sender, "");
                        return true;
                    case "bal":
                    case "balance":
                        if (sender.hasPermission("ws.bal") && Option.ECONOMY_ENABLED && sender instanceof Player) {
                            Player p = (Player) sender;
                            WorldPlayer player = WorldPlayer.getPlayer(p);

                            if (player == null) {
                                player = WorldPlayer.register(p);
                            }

                            player.send(Option.ECONOMY_SWITCH_FORMAT.replace("%amount%", Double.toString(player.getBalance())));
                        }
                }
                break;
            case 4:
            case 5:
                if (args[0].equalsIgnoreCase("eco") && Option.ECONOMY_ENABLED) {
                    Player p = Bukkit.getPlayer(args[2]);

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

                    WorldPlayer player = WorldPlayer.getPlayer(p);

                    if (player == null) {
                        player = WorldPlayer.register(p);
                    }

                    String group = player.getWorldGroup();

                    if (args.length == 5) {
                        group = args[4];
                    }

                    if (Option.getWorlds(group).isEmpty()) {
                        Message.send(sender, WorldServer.MESSAGE_PREFIX + "Couldn't find that world or group!");
                        return true;
                    }

                    switch (args[1].toLowerCase()) {
                        case "set":
                            player.setBalance(amount, group);
                            return true;
                        case "add":
                            player.deposit(group, amount);
                            return true;
                        case "remove":
                            player.withdraw(group, amount);
                            return true;
                    }
                }
                break;
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
            if (sender.hasPermission("ws.eco") && Option.ECONOMY_ENABLED) {
                completions.add("eco");
            }
            if (sender.hasPermission("ws.reload")) {
                completions.add("reload");
            }
            return completions(args[0], completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("eco")) {
                if (sender.hasPermission("ws.eco") && Option.ECONOMY_ENABLED) {
                    completions.add("set");
                    completions.add("add");
                    completions.add("remove");
                }
            }
            return completions(args[1], completions);
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("eco")) {
                if (sender.hasPermission("ws.eco") && Option.ECONOMY_ENABLED) {
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