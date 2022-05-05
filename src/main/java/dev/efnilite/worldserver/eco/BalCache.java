package dev.efnilite.worldserver.eco;

import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.WorldServer;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BalCache {

    private static Map<UUID, Map<String, Double>> balances;

    public static void save(UUID uuid, String group, double amount) {
        Map<String, Double> data = new HashMap<>();
        if (balances.containsKey(uuid)) {
            data = balances.get(uuid);
        }

        data.put(group, amount);
        balances.put(uuid, data);
    }

    public static void read() {
        File folder = new File(WorldServer.getPlugin().getDataFolder() + "/players/");
        if (!(folder.exists())) {
            folder.mkdirs();
            return;
        }

        try {
            for (File file : folder.listFiles()) {
                FileReader reader = new FileReader(file);
                WorldPlayer from = WorldServer.getGson().fromJson(reader, WorldPlayer.class);
                if (from == null) {
                    continue;
                }

                String name = file.getName();
                UUID uuid = UUID.fromString(name.substring(0, name.lastIndexOf('.')));

                balances.put(uuid, from.balances != null ? from.balances : new HashMap<>());
                reader.close();
            }
        } catch (Throwable throwable) {
            WorldServer.logging().stack("Error while reading existing scores", throwable);
        }
    }

    public static double get(UUID uuid, String group) {
        Map<String, Double> data = balances.getOrDefault(uuid, new HashMap<>());
        return data.getOrDefault(group, 0D);
    }
}