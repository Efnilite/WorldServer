package dev.efnilite.worldserver.eco;

import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.worldserver.config.Option;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class BaltopCommand extends ViCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && sender.hasPermission("ws.eco.baltop") && Option.ECONOMY_ENABLED) {

            StringJoiner joiner = new StringJoiner(" ");
            for (String arg : args) {
                joiner.add(arg);
            }

            ((Player) sender).performCommand("ws baltop " + joiner);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] strings) {
        return Collections.emptyList();
    }
}