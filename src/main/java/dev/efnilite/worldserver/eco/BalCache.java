package dev.efnilite.worldserver.eco;

import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.WorldServer;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BalCache {

    public static final Map<UUID, Map<String, Double>> BALANCES = new HashMap<>();

    public static void save(UUID uuid, String group, double amount) {
        Map<String, Double> balance = BALANCES.getOrDefault(uuid, new HashMap<>());
        balance.put(group, amount);
        BALANCES.put(uuid, balance);
    }

    public static void read() {
        File folder = WorldServer.getInFolder("players/");

        if (!(folder.exists())) {
            folder.mkdirs();
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        try {
            for (File file : files) {
                FileReader reader = new FileReader(file);
                WorldPlayer from = WorldServer.getGson().fromJson(reader, WorldPlayer.class);
                if (from == null) {
                    continue;
                }

                String fName = file.getName();
                UUID uuid = UUID.fromString(fName.substring(0, fName.lastIndexOf('.')));

                BALANCES.put(uuid, from.balances != null ? from.balances : new HashMap<>());
                reader.close();
            }
        } catch (Throwable throwable) {
            WorldServer.logging().stack("Error while reading existing scores", throwable);
        }
    }

    public static Set<UUID> getUUIDs() {
        return BALANCES.keySet();
    }

    public static double get(UUID uuid, String group) {
        Map<String, Double> data = BALANCES.getOrDefault(uuid, new HashMap<>());
        return data.getOrDefault(group, 0D);
    }
}