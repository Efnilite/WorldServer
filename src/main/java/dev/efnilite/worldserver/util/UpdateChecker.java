package dev.efnilite.worldserver.util;

import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.config.Verbose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

public class UpdateChecker {

    public void check() {
        Tasks.asyncTask(() -> {
            String latest;
            try {
                latest = getLatestVersion();
            } catch (IOException ex) {
                ex.printStackTrace();
                Verbose.error("Error while trying to fetch latest version!");
                return;
            }
            if (!WorldServer.getInstance().getDescription().getVersion().equals(latest)) {
                Verbose.info("A new version of WorldServer is available to download!");
                Verbose.info("Newest version: " + latest);
                WorldServer.IS_OUTDATED = true;
            } else {
                Verbose.info("WorldServer is currently up-to-date!");
            }
        });
    }

    private String getLatestVersion() throws IOException {
        InputStream stream;
        Verbose.info("Checking for updates...");
        try {
            stream = new URL("https://raw.githubusercontent.com/Efnilite/WorldServer/master/src/main/resources/plugin.yml").openStream();
        } catch (IOException e) {
            Verbose.info("Unable to check for updates!");
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines()
                    .filter(s -> s.contains("version: ") && !s.contains("api"))
                    .collect(Collectors.toList())
                    .get(0)
                    .replace("version: ", "").replace("'", "");
        }
    }
}