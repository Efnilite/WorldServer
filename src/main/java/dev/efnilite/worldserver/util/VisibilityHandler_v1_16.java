package dev.efnilite.worldserver.util;

import org.bukkit.entity.Player;

// from 1.16 to 1.18
public class VisibilityHandler_v1_16 extends VisibilityHandler {

    @Override
    public void show(Player player, Player show) {
        player.showPlayer(plugin, show);
    }

    @Override
    public void hide(Player player, Player hide) {
        player.hidePlayer(plugin, hide);
    }
}
