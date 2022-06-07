package dev.efnilite.worldserver.eco;

import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.worldserver.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class PayCommand extends ViCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && sender.hasPermission("ws.eco.pay") && ConfigValue.ECONOMY_ENABLED) {

            StringJoiner joiner = new StringJoiner(" ");
            for (String arg : args) {
                joiner.add(arg);
            }

            ((Player) sender).performCommand("ws pay " + joiner);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender.hasPermission("ws.eco.pay") && ConfigValue.ECONOMY_ENABLED) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        return completions(args[0], completions);
    }
}