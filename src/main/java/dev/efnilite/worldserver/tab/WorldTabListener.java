package dev.efnilite.worldserver.tab;

import dev.efnilite.vilib.event.EventWatcher;
import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.util.GroupUtil;
import dev.efnilite.worldserver.util.VisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Manages tab.
 */
public class WorldTabListener implements EventWatcher {

    @EventHandler(priority = EventPriority.HIGH)
    public void join(PlayerJoinEvent event) {
        if (!Option.TAB_ENABLED) {
            return;
        }

        Player player = event.getPlayer();

        List<Player> inGroup = GroupUtil.getPlayersInWorldGroup(player.getWorld());

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (inGroup.contains(other)) {
                show(player, Collections.singleton(other));
            } else {
                hide(player, Collections.singleton(other));
            }
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (!Option.TAB_ENABLED) {
            return;
        }

        show(event.getPlayer(), Bukkit.getOnlinePlayers());
    }

    @EventHandler
    public void switchWorld(PlayerChangedWorldEvent event) {
        if (!Option.TAB_ENABLED) {
            return;
        }

        Player player = event.getPlayer();

        hide(player, GroupUtil.getPlayersInWorldGroup(event.getFrom())); // hide from previous world
        show(player, GroupUtil.getPlayersInWorldGroup(player.getWorld())); // show to current world
    }

    private final VisibilityHandler visibilityHandler = VisibilityHandler.getInstance();

    private void hide(Player player, Collection<? extends Player> others) {
        for (Player other : others) {
            visibilityHandler.hide(other, player);
            visibilityHandler.hide(player, other);
        }
    }

    private void show(Player player, Collection<? extends Player> others) {
        for (Player other : others) {
            visibilityHandler.show(other, player);
            visibilityHandler.show(player, other);
        }
    }
}