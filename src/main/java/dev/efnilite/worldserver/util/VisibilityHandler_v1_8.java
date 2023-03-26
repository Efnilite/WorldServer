package dev.efnilite.worldserver.util;

import org.bukkit.entity.Player;

// from 1.8 to 1.12
@SuppressWarnings("deprecation")
public class VisibilityHandler_v1_8 implements VisibilityHandler {

    @Override
    public void show(Player player, Player show) {
        player.showPlayer(show);
    }

    @Override
    public void hide(Player player, Player hide) {
        player.hidePlayer(hide);
    }
}
