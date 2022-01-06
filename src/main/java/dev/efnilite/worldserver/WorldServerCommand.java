package dev.efnilite.worldserver;

import dev.efnilite.worldserver.util.SimpleCommand;
import dev.efnilite.worldserver.util.Tasks;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldServerCommand extends SimpleCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Util.send(sender, "&8&m-----------&r &#468094&lWorldServer &8&m-----------");
            Util.send(sender, "&#468094/ws &f- &7The main command");
            if (sender.hasPermission("ws.reload")) {
                Util.send(sender, "&#468094/ws reload &f- &7Reload the config and commands");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("ws.reload")) {
                    return true;
                }
                Tasks.time("reload");
                WorldServer.getConfiguration().reload();
                Util.send(sender, "&#468094&l> &7Reloaded WorldServer in " + Tasks.end("reload") + "ms!");
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (player.hasPermission("ws.reload")) {
                completions.add("reload");
            }
            return completions(args[0], completions);
        } else {
            return Collections.emptyList();
        }
    }
}