package dev.efnilite.ws.menu;

import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.animation.WaveEastAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.util.SkullSetter;
import dev.efnilite.ws.WorldPlayer;
import dev.efnilite.ws.config.Option;
import dev.efnilite.ws.eco.BalCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;

import static dev.efnilite.ws.eco.EconomyProvider.CURRENCY_FORMAT;

/**
 * A class containing
 */
public class EcoTopMenu {

    /**
     * Shows the leaderboard menu
     *
     * @param player The player
     */
    public static void open(WorldPlayer player) {
        // init vars
        PagedMenu menu = new PagedMenu(4, "<white>Balance Leaderboard");
        List<MenuItem> items = new ArrayList<>();

        Item base = new Item(Material.PLAYER_HEAD, "<#6693E7><bold>#%rank% - %player%")
                .lore("<#5574AF>Amount: <gray>%s%%amount%%".formatted(Option.ECONOMY_CURRENCY_SYMBOL));

        Map<UUID, Double> collected = BalCache.getUUIDs().stream()
                .collect(Collectors.toMap(k -> k, v -> BalCache.get(v, player.getWorldGroup())))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        int rank = 1;
        for (UUID uuid : collected.keySet()) {
            double amount = collected.get(uuid);

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String offlineName = offlinePlayer.getName();
            if (offlineName == null) {
                offlineName = "?";
            }

            int finalRank = rank;
            String finalOfflineName = ChatColor.stripColor(offlineName);
            Item item = base.clone().material(Material.PLAYER_HEAD)
                    .modifyName(name -> name.replace("%rank%", Integer.toString(finalRank))
                            .replace("%player%", finalOfflineName)
                            .replace("%amount%", CURRENCY_FORMAT.format(Double.toString(amount))))
                    .modifyLore(line -> line.replace("%rank%", Integer.toString(finalRank))
                            .replace("%player%", finalOfflineName)
                            .replace("%amount%", CURRENCY_FORMAT.format(Double.toString(amount))));

            // Player head gathering
            ItemStack stack = item.build();
            stack.setType(Material.PLAYER_HEAD);
            if (rank <= 36 && !offlineName.startsWith(".") && offlinePlayer.getName() != null) {
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                SkullSetter.setPlayerHead(offlinePlayer, meta);
                item.meta(meta);
            }

            // add player's own head
            if (uuid.equals(player.player.getUniqueId())) {
                menu.item(30, item.clone());
                item.glowing();
            }

            items.add(item);
            rank++;
        }

        menu.displayRows(0, 1).addToDisplay(items)
                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>»").click(event -> menu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>«").click(event -> menu.page(-1)))
                .item(32, new Item(Material.ARROW, "<#F5A3A3><bold>Close").click(event -> event.getPlayer().closeInventory()))
                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new WaveEastAnimation())
                .open(player.player);
    }

}
