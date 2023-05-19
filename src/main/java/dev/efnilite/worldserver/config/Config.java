package dev.efnilite.worldserver.config;

import dev.efnilite.vilib.lib.configupdater.configupdater.ConfigUpdater;
import dev.efnilite.worldserver.WorldServer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Config management class.
 */
public enum Config {

    CONFIG("config.yml", Arrays.asList("groups",
            "groups-share", "chat-format", "chat-cooldown", "chat-blocked", "economy-currency-names",
            "economy-starting-amount", "chat-join-formats", "chat-leave-formats"));

    /**
     * Reloads all config files.
     */
    public static void reload() {
        for (Config config : values()) {
            config.load();
        }

        // read config stuff
        Option.init();

        WorldServer.logging().info("Loaded all config files");
    }

    /**
     * The {@link FileConfiguration} instance associated with this config file.
     */
    public FileConfiguration fileConfiguration;

    /**
     * The path to this file, incl. plugin folder.
     */
    public final File path;

    /**
     * The name of this file, e.g. config.yml
     */
    public final String fileName;

    /**
     * The sections in the file that will be ignored when updating the keys.
     */
    public final List<String> ignoredSections;

    Config(String fileName, @Nullable List<String> ignoredSections) {
        this.fileName = fileName;
        this.ignoredSections = ignoredSections;
        this.path = WorldServer.getInFolder(fileName);

        if (!path.exists()) {
            WorldServer.getPlugin().saveResource(fileName, false);
            WorldServer.logging().info(String.format("Created config file %s", fileName));
        }

        update();
        load();
    }

    /**
     * Loads the file from disk.
     */
    public void load() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(path);
    }

    /**
     * Updates the file so all keys are present.
     */
    public void update() {
        try {
            ConfigUpdater.update(WorldServer.getPlugin(), fileName, path, ignoredSections);
        } catch (Exception ex) {
            WorldServer.logging().stack("Error while trying to update config file", ex);
        }
    }

    /**
     * @param path The path.
     * @return True when path exists, false if not.
     */
    public boolean isPath(@NotNull String path) {
        return fileConfiguration.isSet(path);
    }

    /**
     * @param path The path.
     * @return The value at path.
     */
    public Object get(@NotNull String path) {
        check(path);

        return fileConfiguration.get(path);
    }

    /**
     * @param path The path.
     * @return The boolean value at path.
     */
    public boolean getBoolean(@NotNull String path) {
        check(path);

        return fileConfiguration.getBoolean(path);
    }

    /**
     * @param path The path.
     * @return The int value at path.
     */
    public int getInt(@NotNull String path) {
        check(path);

        return fileConfiguration.getInt(path);
    }

    /**
     * @param path The path.
     * @return The double value at path.
     */
    public double getDouble(@NotNull String path) {
        check(path);

        return fileConfiguration.getDouble(path);
    }

    /**
     * @param path The path.
     * @return The String value at path.
     */
    @NotNull
    public String getString(@NotNull String path) {
        check(path);

        return fileConfiguration.getString(path, "");
    }

    /**
     * @param path The path.
     * @return The String list value at path.
     */
    @NotNull
    public List<String> getStringList(@NotNull String path) {
        check(path);

        return fileConfiguration.getStringList(path);
    }

    /**
     * @param path The path.
     * @return The int list value at path.
     */
    @NotNull
    public List<Integer> getIntList(@NotNull String path) {
        check(path);

        return fileConfiguration.getIntegerList(path);
    }

    /**
     * Sets value at path to object.
     *
     * @param path   The path.
     * @param object The object.
     */
    public void set(@NotNull String path, Object object) {
        fileConfiguration.set(path, object);
    }

    /**
     * @param path The path.
     * @param deep Whether search should include children of children as well.
     * @return The children nodes from path.
     */
    @NotNull
    public List<String> getChildren(@NotNull String path, boolean... deep) {
        check(path);

        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

        if (section == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(section.getKeys(deep != null));
    }

    // checks if the specified path exists to avoid developer error
    private void check(@NotNull String path) {
        if (!isPath(path)) {
            throw new NoSuchElementException(String.format("Unknown path %s in %s", path, fileName));
        }
    }
}
