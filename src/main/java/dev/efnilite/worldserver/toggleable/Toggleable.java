package dev.efnilite.worldserver.toggleable;

import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.util.VisibilityHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Toggleable {

    protected VisibilityHandler visibilityHandler;

    public Toggleable() {
        this.visibilityHandler = WorldServer.getVisibilityHandler();
    }

    // Gets players in the world group if present or in a single world
    protected List<Player> getPlayersInWorldGroup(World world) {
        List<Player> players = new ArrayList<>();
        String group = Option.getGroupFromWorld(world);
        if (group.equals("")) {
            players.addAll(world.getPlayers());
        } else {
            for (World loopWorld : Option.getWorlds(group)) {
                players.addAll(loopWorld.getPlayers());
            }
        }
        return players;
    }
}
