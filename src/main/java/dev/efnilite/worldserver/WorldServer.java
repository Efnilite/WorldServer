package dev.efnilite.worldserver;

import dev.efnilite.fycore.FyPlugin;
import dev.efnilite.fycore.util.Logging;
import dev.efnilite.fycore.util.Task;
import dev.efnilite.fycore.util.Time;
import dev.efnilite.fycore.util.Version;
import dev.efnilite.worldserver.config.Configuration;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.toggleable.GeneralHandler;
import dev.efnilite.worldserver.toggleable.WorldChatListener;
import dev.efnilite.worldserver.toggleable.WorldSwitchListener;
import dev.efnilite.worldserver.util.UpdateChecker;
import dev.efnilite.worldserver.util.VisibilityHandler;
import dev.efnilite.worldserver.util.VisibilityHandler_v1_13;
import dev.efnilite.worldserver.util.VisibilityHandler_v1_8;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldServer extends FyPlugin {

    public static final String NAME = "<gradient:#3D626F>WorldServer</gradient:#0EACE2>";
    public static final String MESSAGE_PREFIX = NAME + " <#7B7B7B>» <gray>";

    public static boolean IS_OUTDATED;
    private static WorldServer instance;
    private static Configuration configuration;
    private static VisibilityHandler visibilityHandler;

    @Override
    public void enable() {
        instance = this;
        verbosing = false;
        Time.timerStart("enable");

        Logging.info("Registered under version " + Version.getPrettyVersion());

        configuration = new Configuration(this);
        Option.init();

        switch (version) {
            case V1_18:
            case V1_17:
            case V1_16:
            case V1_15:
            case V1_14:
            case V1_13:
                visibilityHandler = new VisibilityHandler_v1_13();
                break;
            case V1_12:
            case V1_11:
            case V1_10:
            case V1_9:
            case V1_8:
                visibilityHandler = new VisibilityHandler_v1_8();
                break;
            default:
                Logging.error("Unsupported version! Please upgrade your server :(");
                Bukkit.getPluginManager().disablePlugin(this);
        }

        registerCommand("worldserver", new WorldServerCommand());
        registerListener(new GeneralHandler());
        registerListener(new WorldChatListener());
        registerListener(new WorldSwitchListener());

        UpdateChecker checker = new UpdateChecker();
        new Task()
                .repeat(8 * 72000)
                .execute(checker::check)
                .run(); // 8 hours

        Metrics metrics = new Metrics(this, 13856);
        metrics.addCustomChart(new SimplePie("chat_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));
        metrics.addCustomChart(new SimplePie("tab_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));

        for (Player player : Bukkit.getOnlinePlayers()) {
            WorldPlayer.register(player);
        }

        Logging.info("Loaded WorldServer " + getDescription().getVersion() + " in " + Time.timerEnd("enable")  + "ms!");
    }

    @Override
    public void disable() {
        for (WorldPlayer wp : WorldPlayer.getPlayers().values()) {
            wp.save(false);
        }
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