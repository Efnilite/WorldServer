package dev.efnilite.worldserver.toggleable;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.vault.VEco;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class WorldEconomyListener implements EventWatcher {

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!Option.ECONOMY_ENABLED) {
            return;
        }
        Player p = event.getPlayer();
        WorldPlayer player = WorldPlayer.getPlayer(p);

        if (player == null) {
            player = WorldPlayer.register(p);
        }

        // register amount of money from previous world
        World from = event.getFrom();
        String fromGroup = Option.getGroupFromWorld(from);

        // player went to same world group so share money amount
        if (player.getWorldGroup().equals(fromGroup)) {
            return;
        }

        double old = VEco.getBalance(player);
        double toSet = player.getBalance();

        VEco.setBalance(player, toSet);
        player.setBalance(old, fromGroup); // save previous

        // switch notification
        if (Option.ECONOMY_SWITCH_NOTIFICATION) {
            player.send(Option.ECONOMY_SWITCH_FORMAT.replace("%amount%", Double.toString(player.getBalance())));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL) // behind the join event in WorldSwitchListener
    public void join(PlayerJoinEvent event) {
        if (!Option.ECONOMY_ENABLED) {
            return;
        }
        Player p = event.getPlayer();
        WorldPlayer player = WorldPlayer.getPlayer(p);

        if (player == null) {
            player = WorldPlayer.register(p);
        }

        VEco.setBalance(player, player.getBalance());
    }
}