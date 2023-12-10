package dev.efnilite.ws.util;

import dev.efnilite.ws.config.Option;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.stream.Collectors;

public class GroupUtil {

    /**
     * @param world The world.
     * @return All players in the world's group.
     */
    public static List<Player> getPlayersInWorldGroup(World world) {
        return getWorlds(getGroupFromWorld(world)).stream()
                .flatMap(loopWorld -> loopWorld.getPlayers().stream())
                .toList();
    }

    /**
     * @param groupName The group/world name
     * @return The worlds from groupName. If there is no associated group, it returns a single-item list with the World instead.
     */
    public static Set<World> getWorlds(String groupName) {
        List<String> worldNames = Option.GROUPS.get(groupName);

        if (worldNames != null) {
            return worldNames.stream()
                    .map(Bukkit::getWorld)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } else {
            return Collections.singleton(Bukkit.getWorld(groupName));
        }
    }

    /**
     * @param world The world
     * @return The associated group, or the world name if not in group.
     */
    @NotNull
    public static String getGroupFromWorld(World world) {
        return getGroupFromWorld(Option.GROUPS, world.getName());
    }

    @TestOnly
    public static String getGroupFromWorld(Map<String, List<String>> groups, String name) {
        for (String group : groups.keySet()) {
            List<String> names = groups.get(group);

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