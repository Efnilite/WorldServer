package dev.efnilite.worldserver;

import dev.efnilite.vilib.ViPlugin;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.vilib.util.Version;
import dev.efnilite.vilib.util.elevator.GitElevator;
import dev.efnilite.vilib.util.elevator.VersionComparator;
import dev.efnilite.worldserver.config.Configuration;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.eco.BalCache;
import dev.efnilite.worldserver.eco.BalCommand;
import dev.efnilite.worldserver.eco.BaltopCommand;
import dev.efnilite.worldserver.eco.PayCommand;
import dev.efnilite.worldserver.toggleable.GeneralHandler;
import dev.efnilite.worldserver.toggleable.WorldChatListener;
import dev.efnilite.worldserver.toggleable.WorldEconomyListener;
import dev.efnilite.worldserver.toggleable.WorldSwitchListener;
import dev.efnilite.worldserver.util.VisibilityHandler;
import dev.efnilite.worldserver.util.VisibilityHandler_v1_13;
import dev.efnilite.worldserver.util.VisibilityHandler_v1_8;
import dev.efnilite.worldserver.vault.*;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldServer extends ViPlugin {

    public static final String NAME = "<gradient:#3D626F>WorldServer</gradient:#0EACE2>";
    public static final String MESSAGE_PREFIX = NAME + " <#7B7B7B>» <gray>";
    private static GitElevator elevator;
    private static WorldServer instance;
    private static Configuration configuration;
    private static VisibilityHandler visibilityHandler;

    @Override
    public void onLoad() {
        VEco.register(this);
    }

    @Override
    public void enable() {
        instance = this;

        logging = new Logging(this);

        Time.timerStart("enable");

        logging.info("Registered under version " + Version.getPrettyVersion());

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
                logging.error("Unsupported version! Please upgrade your server :(");
                Bukkit.getPluginManager().disablePlugin(this);
        }

        registerCommand("worldserver", new WorldServerCommand());
        if (Option.ECONOMY_OVERRIDE_BALANCE_COMMAND) {
            registerCommand("bal", new BalCommand());
        }
        if (Option.ECONOMY_OVERRIDE_PAY_COMMAND) {
            registerCommand("pay", new PayCommand());
        }
        if (Option.ECONOMY_OVERRIDE_BALTOP_COMMAND) {
            registerCommand("baltop", new BaltopCommand());
        }
        registerListener(new GeneralHandler());
        registerListener(new WorldChatListener());
        registerListener(new WorldSwitchListener());
        registerListener(new WorldEconomyListener());

        elevator = new GitElevator("Efnilite/WorldServer", this, VersionComparator.FROM_SEMANTIC, Option.AUTO_UPDATER);

        Metrics metrics = new Metrics(this, 13856);
        metrics.addCustomChart(new SimplePie("chat_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));
        metrics.addCustomChart(new SimplePie("tab_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));
        metrics.addCustomChart(new SimplePie("eco_enabled", () -> Boolean.toString(Option.ECONOMY_ENABLED)));

        for (Player player : Bukkit.getOnlinePlayers()) {
            WorldPlayer.register(player);
        }

        Task.create(this) // save data every 5 minutes
                .delay(5 * 60 * 20)
                .repeat(5 * 60 * 20)
                .execute(() -> {
                    for (WorldPlayer player : WorldPlayer.getPlayers().values()) {
                        player.save(true);
                    }
                })
                .run();

        Task.create(this) // read existing balance caches
                .async()
                .execute(BalCache::read)
                .run();

        // Vault setups
        VChat.register();

        logging.info("Loaded WorldServer " + getDescription().getVersion() + " in " + Time.timerEnd("enable")  + "ms!");
    }

    @Override
    public void disable() {
        for (WorldPlayer wp : WorldPlayer.getPlayers().values()) {
            wp.save(false);
        }
    }

    /**
     * Returns the {@link Logging} belonging to this plugin.
     *
     * @return this plugin's {@link Logging} instance.
     */
    public static Logging logging() {
        return getPlugin().logging;
    }

    public static VisibilityHandler getVisibilityHandler() {
        return visibilityHandler;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static GitElevator getElevator() {
        return elevator;
    }

    public static WorldServer getPlugin() {
        return instance;
    }
}