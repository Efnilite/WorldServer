package dev.efnilite.worldserver;

import dev.efnilite.vilib.ViPlugin;
import dev.efnilite.vilib.lib.bstats.bukkit.Metrics;
import dev.efnilite.vilib.lib.bstats.charts.SimplePie;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.vilib.util.Version;
import dev.efnilite.vilib.util.elevator.GitElevator;
import dev.efnilite.worldserver.chat.WorldChatListener;
import dev.efnilite.worldserver.config.Config;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.eco.*;
import dev.efnilite.worldserver.hook.PlaceholderHook;
import dev.efnilite.worldserver.hook.VaultHook;
import dev.efnilite.worldserver.tab.WorldTabListener;
import dev.efnilite.worldserver.util.Commands;
import dev.efnilite.worldserver.util.Util;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class WorldServer extends ViPlugin {

    public static final String NAME = "<#3D626F>WorldServer";
    public static final String MESSAGE_PREFIX = NAME + " <#7B7B7B>» <gray>";
    public static final String REQUIRED_VILIB_VERSION = "v1.1.0";

    private static WorldServer instance;

    @Override
    public void onLoad() {
        YamlConfiguration c = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        if (c.getBoolean("economy-enabled") && getServer().getPluginManager().getPlugin("Vault") != null) {
            try {
                Class.forName("net.milkbowl.vault.economy.Economy");
                getServer().getServicesManager().register(Economy.class, new EconomyProvider(), this, ServicePriority.High);
                getLogger().info("Registered with Vault!");
            } catch (NoClassDefFoundError | ClassNotFoundException ignored) {

            }
        }
    }

    @Override
    public void enable() {
        instance = this;

        // ----- Check vilib -----

        Plugin vilib = getServer().getPluginManager().getPlugin("vilib");
        if (vilib == null || !vilib.isEnabled()) {
            getLogger().severe("##");
            getLogger().severe("## WorldServer requires vilib to work!");
            getLogger().severe("##");
            getLogger().severe("## Please download it here:");
            getLogger().severe("## https://github.com/Efnilite/vilib/releases/latest");
            getLogger().severe("##");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!Util.isLatest(REQUIRED_VILIB_VERSION, vilib.getDescription().getVersion())) {
            getLogger().severe("##");
            getLogger().severe("## WorldServer requires *a newer version* of vilib to work!");
            getLogger().severe("##");
            getLogger().severe("## Please download it here:");
            getLogger().severe("## https://github.com/Efnilite/vilib/releases/latest");
            getLogger().severe("##");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // ----- Start time -----

        Time.timerStart("ws enable");

        // ----- Configurations -----

        Config.reload();

        logging.info("Registered under version " + Version.getPrettyVersion());

        registerCommand("worldserver", new WorldServerCommand());
        if (Option.ECONOMY_ENABLED && Option.ECONOMY_OVERRIDE_BALANCE_COMMAND) {
            Commands.registerToMap("bal", new BalCommand());
            Commands.registerToMap("balance", new BalCommand());
        }
        if (Option.ECONOMY_ENABLED && Option.ECONOMY_OVERRIDE_PAY_COMMAND) {
            Commands.registerToMap("pay", new PayCommand());
            Commands.registerToMap("transfer", new PayCommand());
        }
        if (Option.ECONOMY_ENABLED && Option.ECONOMY_OVERRIDE_BALTOP_COMMAND) {
            Commands.registerToMap("baltop", new BaltopCommand());
            Commands.registerToMap("balancetop", new BaltopCommand());
        }
        registerListener(new GeneralListener());
        registerListener(new WorldChatListener());
        registerListener(new WorldTabListener());
        registerListener(new WorldEconomyListener());

        Metrics metrics = new Metrics(this, 13856);
        metrics.addCustomChart(new SimplePie("chat_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));
        metrics.addCustomChart(new SimplePie("tab_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));
        metrics.addCustomChart(new SimplePie("eco_enabled", () -> Boolean.toString(Option.ECONOMY_ENABLED)));

        Bukkit.getOnlinePlayers().forEach(WorldPlayer::register);

        Task.create(this).delay(5 * 60 * 20).repeat(5 * 60 * 20).execute(() -> {
            WorldPlayer.PLAYERS.values().forEach(player -> player.save(true));
        }).run(); // save data every 5 minutes

        Task.create(this).async().execute(BalCache::read).run(); // read existing balance caches

        // Vault setups
        VaultHook.register();
        PlaceholderHook.register();

        logging.info(String.format("Loaded WorldServer v%s in %dms!", getDescription().getVersion(), Time.timerEnd("ws enable")));
    }

    @Override
    public void disable() {
        WorldPlayer.PLAYERS.values().forEach(wp -> WorldPlayer.unregister(wp.player, false));
    }

    @Override
    public @Nullable GitElevator getElevator() {
        return null;
    }

    @Nullable
    public GitElevator getElevatorInstance() {
        if (elevator == null) {
            elevator = getElevator();
        }

        return elevator;
    }

    /**
     * @param child The file name.
     * @return A file from within the plugin folder.
     */
    public static File getInFolder(String child) {
        return new File(getPlugin().getDataFolder(), child);
    }

    /**
     * Returns the {@link Logging} belonging to this plugin.
     *
     * @return this plugin's {@link Logging} instance.
     */
    public static Logging logging() {
        return getPlugin().logging;
    }

    public static WorldServer getPlugin() {
        return instance;
    }
}