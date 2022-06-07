package dev.efnilite.worldserver.config;

import com.tchristofferson.configupdater.ConfigUpdater;
import dev.efnilite.worldserver.WorldServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * An utilities class for the Configuration
 */
public class Configuration {

    private final Plugin plugin;
    private final HashMap<String, FileConfiguration> files;

    /**
     * Create a new instance
     */
    public Configuration(Plugin plugin) {
        this.plugin = plugin;
        files = new HashMap<>();

        String[] defaultFiles = new String[]{"config.yml"};

        new File(plugin.getDataFolder(), "players").mkdirs();

        File folder = plugin.getDataFolder();
        if (!new File(folder, defaultFiles[0]).exists()) {
            plugin.getDataFolder().mkdirs();

            for (String file : defaultFiles) {
                plugin.saveResource(file, false);
            }
            WorldServer.logging().info("Downloaded all config files");
        }
        for (String file : defaultFiles) {
            try {
                ConfigUpdater.update(plugin, file, new File(plugin.getDataFolder(), file),
                        Arrays.asList("groups", "chat-format", "chat-cooldown", "chat-blocked", "economy-currency-names",
                                "economy-starting-amount", "chat-join-formats", "chat-leave-formats"));
            } catch (IOException ex) {
                ex.printStackTrace();
                WorldServer.logging().error("Error while trying to update config");
            }
            FileConfiguration configuration = this.getFile(folder + "/" + file);
            files.put(file.replaceAll("(.+/|.yml)", ""), configuration);
        }
    }

    public void reload() {
        ConfigValue.invalidateCaches();

        files.put("config", YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/config.yml")));

        ConfigValue.init();
    }

    /**
     * Get a file
     */
    public FileConfiguration getFile(String file) {
        FileConfiguration config;
        if (files.get(file) == null) {
            config = YamlConfiguration.loadConfiguration(new File(file));
            files.put(file, config);
        } else {
            config = files.get(file);
        }
        return config;
    }
}