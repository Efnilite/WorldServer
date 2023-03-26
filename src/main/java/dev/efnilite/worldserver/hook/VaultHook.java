package dev.efnilite.worldserver.hook;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VaultHook {

    private static @Nullable Chat chat;

    /**
     * Registers the chat component of Vault
     */
    public static void register() {
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
     * @param player The player
     * @return the player prefix
     */
    public static @NotNull String getPrefix(Player player) {
        return chat != null ? chat.getPlayerPrefix(player.getWorld().getName(), player) : "";
    }

    /**
     * Returns the player's suffix if the chat component of Vault is found
     *
     * @param player The player
     * @return the player suffix
     */
    public static @NotNull String getSuffix(Player player) {
        return chat != null ? chat.getPlayerSuffix(player.getWorld().getName(), player) : "";
    }
}
