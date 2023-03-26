package dev.efnilite.worldserver.group;

import dev.efnilite.worldserver.config.Option;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupUtil {

    /**
     * @param world The world.
     * @return All players in the world's group.
     */
    public static List<Player> getPlayersInWorldGroup(World world) {
        List<Player> players = new ArrayList<>();
        String group = getGroupFromWorld(world);

        for (World loopWorld : getWorlds(group)) {
            players.addAll(loopWorld.getPlayers());
        }
        return players;
    }

    /**
     * @param groupName The group/world name
     * @return The worlds from groupName. If there is no associated group, it returns a single-item list with the World instead.
     */
    public static List<World> getWorlds(String groupName) {
        List<World> worlds = new ArrayList<>();
        List<String> worldNames = Option.GROUPS.get(groupName);

        if (worldNames != null) {
            for (String name : worldNames) {
                World world = Bukkit.getWorld(name);
                if (world == null) {
                    continue;
                }
                worlds.add(world);
            }
        } else {
            worlds.add(Bukkit.getWorld(groupName));
        }

        return worlds;
    }

    /**
     * @param world The world
     * @return The associated group, or the world name if not in group.
     */
    @NotNull
    public static String getGroupFromWorld(World world) {
        String name = world.getName();

        for (String group : Option.GROUPS.keySet()) {
            List<String> names = Option.GROUPS.get(group);

            if (names == null) {
                return name;
            }

            for (String loopWorld : names) {
                if (name.equals(loopWorld)) {
                    return group;
                }
            }
        }
        return name;
    }

}
