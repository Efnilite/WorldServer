package dev.efnilite.worldserver;

import dev.efnilite.worldserver.config.Configuration;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.config.Verbose;
import dev.efnilite.worldserver.toggleable.WorldChatListener;
import dev.efnilite.worldserver.toggleable.WorldSwitchListener;
import dev.efnilite.worldserver.util.*;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldServer extends JavaPlugin {

    private static WorldServer instance;
    private static Configuration configuration;
    private static VisibilityHandler visibilityHandler;

    @Override
    public void onEnable() {
        instance = this;

        Verbose.init(this);
        configuration = new Configuration(this);
        Option.init();

        switch (Util.getVersion().substring(0, 5)) {
            case "v1_18":
            case "v1_17":
            case "v1_16":
                visibilityHandler = new VisibilityHandler_v1_16();
                break;
            case "v1_15":
            case "v1_14":
            case "v1_13":
            case "v1_12":
            case "v1_11":
            case "v1_10":
            case "v1_9_":
                visibilityHandler = new VisibilityHandler_v1_9();
                break;
            default:
                Verbose.error("Unsupported version! Please upgrade your server :(");
                Bukkit.getPluginManager().disablePlugin(this);
        }

        Metrics metrics = new Metrics(this, 13856);
        metrics.addCustomChart(new SimplePie("chat_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));
        metrics.addCustomChart(new SimplePie("tab_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));

        SimpleCommand.register("worldserver",  new WorldServerCommand());

        Bukkit.getPluginManager().registerEvents(new WorldChatListener(Option.CHAT_ENABLED), this);
        Bukkit.getPluginManager().registerEvents(new WorldSwitchListener(Option.TAB_ENABLED), this);

        Verbose.info("Loaded WorldServer " + getDescription().getVersion() + " by Efnilite");
    }

    public static VisibilityHandler getVisibilityHandler() {
        return visibilityHandler;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static WorldServer getInstance() {
        return instance;
    }
}