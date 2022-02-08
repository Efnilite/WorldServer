package dev.efnilite.worldserver;

import dev.efnilite.fycore.chat.Message;
import dev.efnilite.fycore.command.FyCommand;
import dev.efnilite.fycore.config.ConfigOption;
import dev.efnilite.fycore.util.Time;
import dev.efnilite.worldserver.config.Option;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldServerCommand extends FyCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Message.send(sender, "");
            Message.send(sender, "<dark_gray><strikethrough>-----------&r " + WorldServer.NAME + " <dark_gray><strikethrough>-----------");
            Message.send(sender, "");
            Message.send(sender, "<gray>/ws <dark_gray>- The main command");
            Message.send(sender, "<gray>/ws permissions<dark_gray>- Get all permissions");
            if (sender.hasPermission("ws.mute.global")) {
                Message.send(sender, "<gray>/ws muteglobal <dark_gray>- Mute or unmute the global chat");
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
            } else if (args[0].equalsIgnoreCase("muteglobal")) {
                if (!sender.hasPermission("ws.reload")) {
                    return true;
                }

                if (Option.GLOBAL_CHAT_ENABLED) {
                    Option.GLOBAL_CHAT_ENABLED = false;
                    Message.send(sender, WorldServer.MESSAGE_PREFIX + "Muted global chat!");
                } else {
                    Option.GLOBAL_CHAT_ENABLED = true;
                    Message.send(sender, WorldServer.MESSAGE_PREFIX + "Unmuted global chat!");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("permissions")) {

                Message.send(sender, "");
                Message.send(sender, "<dark_gray><strikethrough>-----------&r <gradient:#3D626F>Permissions</gradient:#0EACE2> <dark_gray><strikethrough>-----------");
                Message.send(sender, "");
                Message.send(sender, "<gray>ws.reload <dark_gray>- Reloads the config");
                Message.send(sender, "<gray>ws.mute.global <dark_gray>- For (un)muting global chat");
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
            if (sender.hasPermission("ws.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("ws.mute.global")) {
                completions.add("muteglobal");
            }
            return completions(args[0], completions);
        } else {
            return Collections.emptyList();
        }
    }
}