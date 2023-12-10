package dev.efnilite.ws.eco;

import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.ws.config.Option;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PayCommand extends ViCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && sender.hasPermission("ws.eco.pay") && Option.ECONOMY_ENABLED) {
            ((Player) sender).performCommand(String.format("ws pay %s", String.join(" ", args)));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ws.eco.pay") || !Option.ECONOMY_ENABLED) {
            return Collections.emptyList();
        }
        return completions(args[0], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    }
}