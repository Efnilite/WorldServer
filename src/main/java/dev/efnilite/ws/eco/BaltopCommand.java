package dev.efnilite.ws.eco;

import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.ws.config.Option;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BaltopCommand extends ViCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && sender.hasPermission("ws.eco.baltop") && Option.ECONOMY_ENABLED) {
            ((Player) sender).performCommand(String.format("ws baltop %s", String.join(" ", args)));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] strings) {
        return Collections.emptyList();
    }
}