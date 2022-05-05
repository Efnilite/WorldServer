package dev.efnilite.worldserver.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VChat {

    private static @Nullable Chat chat;

    public static void registerEco(Plugin plugin) {
        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
            Bukkit.getServer().getServicesManager().register(Economy.class, new EconomyProvider(), plugin, ServicePriority.High);
            plugin.getLogger().info("Registered with Vault!");
        } catch (ClassNotFoundException ignored) {

        }
    }

    /**
     * Registers the chat component of Vault
     */
    public static void registerChat() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            chat = rsp.getProvider();
        }
    }

    /**
     * Returns the player's prefix if the chat component of Vault is found
     *
     * @param   player
     *          The player
     *
     * @return the player prefix
     */
    public static @NotNull String getPrefix(Player player) {
        return chat != null ? chat.getPlayerPrefix(player.getWorld().getName(), player) : "";
    }

    /**
     * Returns the player's suffix if the chat component of Vault is found
     *
     * @param   player
     *          The player
     *
     * @return the player suffix
     */
    public static @NotNull String getSuffix(Player player) {
        return chat != null ? chat.getPlayerSuffix(player.getWorld().getName(), player) : "";
    }

    public static @Nullable Chat getChat() {
        return chat;
    }
}
