package dev.efnilite.worldserver.eco;

import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.worldserver.config.Option;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class WBalCommand extends ViCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && sender.hasPermission("ws.bal") && Option.ECONOMY_ENABLED) {
            ((Player) sender).performCommand("/ws bal");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] strings) {
        return Collections.emptyList();
    }
}
