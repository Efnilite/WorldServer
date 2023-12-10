package dev.efnilite.ws;

import dev.efnilite.vilib.ViPlugin;
import dev.efnilite.vilib.bstats.bukkit.Metrics;
import dev.efnilite.vilib.bstats.charts.SimplePie;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.vilib.util.elevator.GitElevator;
import dev.efnilite.ws.chat.WorldChatListener;
import dev.efnilite.ws.config.Config;
import dev.efnilite.ws.config.Option;
import dev.efnilite.ws.eco.*;
import dev.efnilite.ws.hook.PlaceholderHook;
import dev.efnilite.ws.hook.VaultHook;
import dev.efnilite.ws.tab.WorldTabListener;
import dev.efnilite.ws.util.BukkitCommand;
import dev.efnilite.ws.util.Commands;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashSet;

public class WorldServer extends ViPlugin {

    public static final String NAME = "<#3D626F>WorldServer";
    public static final String MESSAGE_PREFIX = NAME + " <#7B7B7B>» <gray>";

    private static WorldServer instance;
    private static Logging logging;


    /**
     * @param child The file name.
     * @return A file from within the plugin folder.
     */
    public static File getInFolder(String child) {
        return new File(getPlugin().getDataFolder(), child);
    }

    /**
     * @return this plugin's {@link Logging} instance.
     */
    public static Logging logging() {
        return logging;
    }

    public static WorldServer getPlugin() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        logging = new Logging(this);

        YamlConfiguration c = YamlConfiguration.loadConfiguration(getInFolder("config.yml"));

        if (!c.getBoolean("economy-enabled") || getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
            getServer().getServicesManager().register(Economy.class, new EconomyProvider(), this, ServicePriority.High);
            logging.info("Registered with Vault!");
        } catch (NoClassDefFoundError | ClassNotFoundException ignored) {

        }
    }

    @Override
    public void enable() {

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

        Menu.init(this);

        // ----- Start time -----

        Time.timerStart("ws enable");

        // ----- Configurations -----

        Config.reload();

        registerCommand("worldserver", new WorldServerCommand());
        if (Option.ECONOMY_ENABLED && Option.ECONOMY_OVERRIDE_BALANCE_COMMAND) {
            registerCommand(new BalCommand(), "bal", "balance", "wsbal");
        }
        if (Option.ECONOMY_ENABLED && Option.ECONOMY_OVERRIDE_PAY_COMMAND) {
            registerCommand(new PayCommand(), "pay", "transfer", "wspay");
        }
        if (Option.ECONOMY_ENABLED && Option.ECONOMY_OVERRIDE_BALTOP_COMMAND) {
            registerCommand(new BaltopCommand(), "baltop", "balancetop", "wsbaltop");
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

        Task.create(this) // save data every 5 minutes
                .delay(5 * 60 * 20)
                .repeat(5 * 60 * 20)
                .execute(() -> WorldPlayer.PLAYERS.values().forEach(player -> player.save(true)))
                .run();

        Task.create(this).async().execute(BalCache::read).run(); // read existing balance caches

        // Vault setups
        VaultHook.register();
        PlaceholderHook.register();

        logging.info("Loaded WorldServer v%s in %dms!".formatted(getDescription().getVersion(), Time.timerEnd("ws enable")));
    }

    private void registerCommand(CommandExecutor executor, String... aliases) {
        for (String alias : aliases) {
            Commands.add(alias, new BukkitCommand(alias, executor));
        }
    }

    @Override
    public void disable() {
        new HashSet<>(WorldPlayer.PLAYERS.values()).forEach(wp -> WorldPlayer.unregister(wp.player, false));
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
}