package dev.efnilite.worldserver.util;

import org.bukkit.entity.Player;

// from 1.9 to 1.15
@SuppressWarnings("deprecation")
public class VisibilityHandler_v1_9 extends VisibilityHandler {

    @Override
    public void show(Player player, Player show) {
        player.showPlayer(show);
    }

    @Override
    public void hide(Player player, Player hide) {
        player.hidePlayer(hide);
    }
}
