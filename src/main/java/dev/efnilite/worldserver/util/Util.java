package dev.efnilite.worldserver.util;

import dev.efnilite.vilib.chat.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Util {

    public static final NumberFormat CURRENCY_FORMAT = NumberFormat.getInstance(Locale.US);

    static {
        CURRENCY_FORMAT.setRoundingMode(RoundingMode.FLOOR);
        CURRENCY_FORMAT.setGroupingUsed(true);
        CURRENCY_FORMAT.setMinimumFractionDigits(2);
        CURRENCY_FORMAT.setMaximumFractionDigits(2);
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