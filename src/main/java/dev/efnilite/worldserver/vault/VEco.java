package dev.efnilite.worldserver.vault;

import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.WorldServer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

public class VEco {

    private static Economy eco;

    public static void register(Plugin plugin) {
        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
            eco = new EconomyProvider();
            Bukkit.getServer().getServicesManager().register(Economy.class, eco, plugin, ServicePriority.High);
            plugin.getLogger().info("Registered with Vault!");
        } catch (ClassNotFoundException ignored) {

        }
    }

    public static void setBalance(WorldPlayer player, double to) {
        double current = eco.getBalance(player.getPlayer());

        if (current > to) { // 5 = x + 10 gives x = -5, thus subtract
            EconomyResponse response = eco.withdrawPlayer(player.getPlayer(), current - to);
            if (!response.transactionSuccess()) {
                if (response.errorMessage != null && response.errorMessage.toLowerCase().contains("loan")) {
                    WorldServer.logging().error("There was an error while updating " + player.getPlayer().getName() + "'s balance.");
                    WorldServer.logging().error("Negative balances are not allowed with this economy!");
                    WorldServer.logging().error("Their balance of " + current + " will be set to 0.");
                    eco.withdrawPlayer(player.getPlayer(), current);
                }
            }
        } else if (current < to) { // 15 = x + 10 gives x = 5, thus add
            EconomyResponse response = eco.depositPlayer(player.getPlayer(), to - current);
            if (!response.transactionSuccess()) {
                WorldServer.logging().error("There was an error while updating " + player.getPlayer().getName() + "'s balance.");
                WorldServer.logging().error("Their balance of " + current + " could not be updated to " + to + ".");
            }
        }
    }

    public static double getBalance(WorldPlayer player) {
        return eco != null ? eco.getBalance(player.getPlayer()) : 0;
    }

    public static Economy getEco() {
        return eco;
    }
}
