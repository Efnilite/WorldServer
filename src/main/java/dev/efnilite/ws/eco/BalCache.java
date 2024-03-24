package dev.efnilite.ws.eco;

import dev.efnilite.ws.WorldPlayer;
import dev.efnilite.ws.WorldServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class BalCache {

    /**
     * @return An immutable list of all {@link java.util.UUID}s present in the balance sheet.
     */
    public static Set<UUID> getUUIDs() {
        return new HashSet<>(BALANCES.keySet());
    }

    public static double get(UUID uuid, String group) {
        Map<String, Double> data = BALANCES.getOrDefault(uuid, new HashMap<>());
        return data.getOrDefault(group, 0D);
    }

    public static final Map<UUID, Map<String, Double>> BALANCES = new HashMap<>();

    public static void saveAll(UUID uuid, String group, double amount) {
        Map<String, Double> balance = BALANCES.getOrDefault(uuid, new HashMap<>());
        balance.put(group, amount);
        BALANCES.put(uuid, balance);
    }

    private static File[] getPlayerFiles() {
        File folder = WorldServer.getInFolder("players/");

        if (!(folder.exists())) {
            folder.mkdirs();
            return new File[]{};
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return new File[]{};
        }

        return files;
    }

    public static void read() {
        for (File file : getPlayerFiles()) {
            try {
                FileReader reader = new FileReader(file);

                WorldPlayer from = WorldServer.getGson().fromJson(reader, WorldPlayer.class);
                if (from == null) {
                    continue;
                }

                String name = file.getName();
                UUID uuid = UUID.fromString(name.substring(0, name.lastIndexOf('.')));

                BALANCES.put(uuid, from.balances != null ? from.balances : new HashMap<>());
                reader.close();
            } catch (Exception ex) {
                WorldServer.logging().stack("Couldn't read data of %s".formatted(file.getName()), ex);
            }
        }
    }

    public static void saveAll() {
        for (File file : getPlayerFiles()) {
            try {
                FileReader reader = new FileReader(file);
                WorldPlayer from = WorldServer.getGson().fromJson(reader, WorldPlayer.class);

                if (from == null) {
                    continue;
                }

                String fName = file.getName();
                UUID uuid = UUID.fromString(fName.substring(0, fName.lastIndexOf('.')));

                from.balances = BALANCES.get(uuid);

                try (FileWriter writer = new FileWriter(file)) {
                    WorldServer.getGson().toJson(from, writer);
                }

                reader.close();
            } catch (Exception ex) {
                WorldServer.logging().stack("Couldn't write data of %s".formatted(file.getName()), ex);
            }
        }
    }
}