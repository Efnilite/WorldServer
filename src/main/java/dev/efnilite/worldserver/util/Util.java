package dev.efnilite.worldserver.util;

import dev.efnilite.fycore.util.colour.Colours;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * Colors strings
     *
     * @param   strings
     *          The strings
     *
     * @return the strings
     */
    public static String[] colour(String... strings) {
        String[] ret = new String[strings.length];
        int i = 0;
        for (String string : strings) {
            ret[i++] = Util.colour(string);
        }
        return ret;
    }

    public static List<String> colour(List<String> strings) {
        List<String> ret = new ArrayList<>();
        for (String string : strings) {
            ret.add(Util.colour(string));
        }
        return ret;
    }

    /**
     * Color something
     */
    public static String colour(String string) {
        if (!string.equals("")) {
            return ChatColor.translateAlternateColorCodes('&', Colours.colour(string));
        }
        return string;
    }

    public static void send(CommandSender sender, String... message) {
        sender.sendMessage(Util.colour(message));
    }

    /**
     * Gets the size of a ConfigurationSection
     *
     * @param   file
     *          The file
     *
     * @param   path
     *          The path
     */
    public static @Nullable List<String> getNode(FileConfiguration file, String path) {
        ConfigurationSection section = file.getConfigurationSection(path);
        if (section == null) {
            return null;
        }
        return new ArrayList<>(section.getKeys(false));
    }

    /**
     * Gets the NMS version
     *
     * @return the nms version
     */
    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }
}