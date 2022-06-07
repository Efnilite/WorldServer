package dev.efnilite.worldserver.menu;

import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.animation.WaveWestAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.util.SkullSetter;
import dev.efnilite.vilib.util.Unicodes;
import dev.efnilite.vilib.util.collections.Sorting;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.ConfigValue;
import dev.efnilite.worldserver.eco.BalCache;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

/**
 * A class containing
 */
public class EcoTopMenu {

    /**
     * Shows the leaderboard menu
     *
     * @param   player
     *          The player
     */
    public static void open(WorldPlayer player) {
        // init vars
        PagedMenu menu = new PagedMenu(4, "<white>Balance Leaderboard");
        List<MenuItem> items = new ArrayList<>();

        int rank = 1;
        Item base = new Item(Material.PLAYER_HEAD, "<#6693E7><bold>#%rank% - %player%")
                .lore("<#5574AF>Amount: <gray>" + ConfigValue.ECONOMY_CURRENCY_SYMBOL + "%amount%");

        Set<UUID> uuids = BalCache.getUUIDs();
        Map<UUID, Double> values = new HashMap<>();
        for (UUID uuid : uuids) {
            double amount = BalCache.get(uuid, player.getWorldGroup());

            values.put(uuid, amount);
        }
        values = Sorting.mapValues(values, Comparator.reverseOrder());

        for (UUID uuid : values.keySet()) {
            double amount = values.get(uuid);

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String offlineName = offlinePlayer.getName();
            if (offlineName == null) {
                offlineName = "?";
            }

            int finalRank = rank;
            String finalOfflineName = offlineName;
            Item item = base.clone()
                    .material(Material.PLAYER_HEAD)
                    .modifyName(name -> name
                            .replace("%rank%", Integer.toString(finalRank))
                            .replace("%player%", finalOfflineName)
                            .replace("%amount%", Double.toString(amount)))
                    .modifyLore(line -> line
                            .replace("%rank%", Integer.toString(finalRank))
                            .replace("%player%", finalOfflineName)
                            .replace("%amount%", Double.toString(amount)));

            // Player head gathering
            ItemStack stack = item.build();
            stack.setType(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            if (meta == null) {
                continue;
            }
            SkullSetter.setPlayerHead(offlinePlayer, meta);
            item.meta(meta);

            // add player's own head
            if (uuid.equals(player.getPlayer().getUniqueId())) {
                menu.item(30, item.clone());
                item.glowing();
            }

            items.add(item);
            rank++;
        }

        menu
                .displayRows(0, 1)
                .addToDisplay(items)

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT) // next page
                        .click(event -> menu.page(1)))

                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
                        .click(event -> menu.page(-1)))

                .item(32, new Item(Material.ARROW, "<#F5A3A3><bold>Close")
                        .click(event -> event.getPlayer().closeInventory()))

                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new WaveWestAnimation())
                .open(player.getPlayer());
    }

}
