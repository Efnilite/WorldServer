package dev.efnilite.worldserver.toggleable;

import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.config.ConfigValue;
import dev.efnilite.worldserver.util.VisibilityHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the chat/tab listeners
 */
public abstract class Toggleable {

    protected VisibilityHandler visibilityHandler;

    public Toggleable() {
        this.visibilityHandler = WorldServer.getVisibilityHandler();
    }

    // Gets players in the world group if present or in a single world
    protected List<Player> getPlayersInWorldGroup(World world) {
        List<Player> players = new ArrayList<>();
        String group = ConfigValue.getGroupFromWorld(world);

        for (World loopWorld : ConfigValue.getWorlds(group)) {
            players.addAll(loopWorld.getPlayers());
        }
        return players;
    }
}
