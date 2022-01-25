package dev.efnilite.worldserver.util;

import dev.efnilite.fycore.util.Logging;
import dev.efnilite.fycore.util.Task;
import dev.efnilite.worldserver.WorldServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

public class UpdateChecker {

    public void check() {
        new Task()
                .async()
                .execute(() -> {
                    String latest;
                    try {
                        latest = getLatestVersion();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        Logging.error("Error while trying to fetch latest version!");
                        return;
                    }
                    if (!WorldServer.getInstance().getDescription().getVersion().equals(latest)) {
                        Logging.info("A new version of WorldServer is available to download!");
                        Logging.info("Newest version: " + latest);
                        WorldServer.IS_OUTDATED = true;
                    } else {
                        Logging.info("WorldServer is currently up-to-date!");
                    }
                }).run();
    }

    private String getLatestVersion() throws IOException {
        InputStream stream;
        Logging.info("Checking for updates...");
        try {
            stream = new URL("https://raw.githubusercontent.com/Efnilite/WorldServer/master/src/main/resources/plugin.yml").openStream();
        } catch (IOException e) {
            Logging.info("Unable to check for updates!");
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