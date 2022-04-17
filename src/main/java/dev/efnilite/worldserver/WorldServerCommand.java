package dev.efnilite.worldserver;

import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.vilib.util.Time;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldServerCommand extends ViCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Message.send(sender, "");
            Message.send(sender, "<dark_gray><strikethrough>-----------&r " + WorldServer.NAME + " <dark_gray><strikethrough>-----------");
            Message.send(sender, "");
            Message.send(sender, "<gray>/ws <dark_gray>- The main command");
            Message.send(sender, "<gray>/ws permissions<dark_gray>- Get all permissions");
            if (sender.hasPermission("ws.mute.global")) {
                Message.send(sender, "<gray>/ws menu <dark_gray>- Change all settings quickly");
            }
            if (sender.hasPermission("ws.reload")) {
                Message.send(sender, "<gray>/ws reload <dark_gray>- Reload the config and commands");
            }
            Message.send(sender, "");
            Message.send(sender, "<dark_gray><strikethrough>---------------------------------");
            Message.send(sender, "");
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("ws.reload")) {
                    return true;
                }
                Time.timerStart("reload");
                WorldServer.getConfiguration().reload();
                Message.send(sender, WorldServer.MESSAGE_PREFIX + "Reloaded WorldServer in " + Time.timerEnd("reload") + "ms!");
                return true;
            } else if (args[0].equalsIgnoreCase("menu")) {
                if (sender instanceof Player && sender.hasPermission("ws.menu")) {
                    WorldServerMenu.openMainMenu((Player) sender);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("permissions")) {

                Message.send(sender, "");
                Message.send(sender, "<dark_gray><strikethrough>-----------&r <gradient:#3D626F>Permissions</gradient:#0EACE2> <dark_gray><strikethrough>-----------");
                Message.send(sender, "");
                Message.send(sender, "<gray>ws.reload <dark_gray>- Reloads the config");
                Message.send(sender, "<gray>ws.menu <dark_gray>- For opening and viewing the menu");
                Message.send(sender, "<gray>ws.spy <dark_gray>- For spying on what everyone in every world is saying. This requires the ws.menu permission.");
                Message.send(sender, "<gray>ws.option.global-chat <dark_gray>- For changing global chat settings");
                Message.send(sender, "<gray>ws.option.chat <dark_gray>- For changing chat settings");
                Message.send(sender, "<gray>ws.option.tab <dark_gray>- For changing tab settings");
                Message.send(sender, "");
                Message.send(sender, "<dark_gray><strikethrough>---------------------------------");
                Message.send(sender, "");
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("permissions");
            if (sender.hasPermission("ws.menu")) {
                completions.add("menu");
            }
            if (sender.hasPermission("ws.reload")) {
                completions.add("reload");
            }
            return completions(args[0], completions);
        } else {
            return Collections.emptyList();
        }
    }
}