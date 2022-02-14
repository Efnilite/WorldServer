package dev.efnilite.worldserver.util;

import dev.efnilite.fycore.chat.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Util {

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
            return ChatColor.translateAlternateColorCodes('&', Message.parseFormatting(string));
        }
        return string;
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
}