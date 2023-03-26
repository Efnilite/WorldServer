package dev.efnilite.worldserver.util;

import dev.efnilite.worldserver.WorldServer;
import org.bukkit.entity.Player;

// from 1.13 to 1.18
public class VisibilityHandler_v1_13 implements VisibilityHandler {

    @Override
    public void show(Player player, Player show) {
        player.showPlayer(WorldServer.getPlugin(), show);
    }

    @Override
    public void hide(Player player, Player hide) {
        player.hidePlayer(WorldServer.getPlugin(), hide);
    }
}
