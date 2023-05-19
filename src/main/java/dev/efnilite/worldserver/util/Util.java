package dev.efnilite.worldserver.util;

import dev.efnilite.vilib.util.Strings;
import dev.efnilite.worldserver.hook.PlaceholderHook;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Util {

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(Strings.colour(sender instanceof Player ? PlaceholderHook.translate((Player) sender, message) : message));
    }

    public static boolean isLatest(String latest, String current) {
        return toVersion(latest) <= toVersion(current);
    }

    private static double toVersion(String version) {
        String stripped = version.toLowerCase().replace("v", "").replace(".", "");

        return Double.parseDouble(stripped) / stripped.length();
    }
}