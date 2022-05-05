//package dev.efnilite.worldserver.menu;
//
//import dev.efnilite.ip.player.ParkourUser;
//import dev.efnilite.vilib.inventory.PagedMenu;
//import dev.efnilite.vilib.inventory.animation.WaveWestAnimation;
//import dev.efnilite.vilib.inventory.item.Item;
//import dev.efnilite.vilib.inventory.item.MenuItem;
//import dev.efnilite.vilib.util.SkullSetter;
//import dev.efnilite.vilib.util.Unicodes;
//import dev.efnilite.worldserver.WorldPlayer;
//import dev.efnilite.worldserver.eco.BalCache;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.SkullMeta;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
///**
// * A class containing
// */
//public class EcoTopMenu {
//
//    /**
//     * Shows the leaderboard menu
//     *
//     * @param   player
//     *          The player
//     */
//    public static void open(WorldPlayer player) {
//        ParkourUser.initHighScores(); // make sure scores are enabled
//
//        // init vars
//        PagedMenu menu = new PagedMenu(4, "<white>Balance top");
//        List<MenuItem> items = new ArrayList<>();
//
//        int rank = 1;
//        Item base = new Item(Material.PLAYER_HEAD, "<#4DF019>%p");
//        for (UUID uuid : uuids) {
//            double amount = BalCache.get(uuid, player.getWorldGroup());
//            if (amount <= 0) {
//                continue;
//            }
//
//            int finalRank = rank;
//            Item item = base.clone()
//                    .material(Material.PLAYER_HEAD)
//                    .modifyName(name -> name.replace("%r", Integer.toString(finalRank))
//                            .replace("%s", Integer.toString(ParkourUser.getHighestScore(uuid)))
//                            .replace("%p", highscore.name())
//                            .replace("%t", highscore.time())
//                            .replace("%d", highscore.difficulty()))
//                    .modifyLore(line -> line.replace("%r", Integer.toString(finalRank))
//                            .replace("%s", Integer.toString(ParkourUser.getHighestScore(uuid)))
//                            .replace("%p", highscore.name())
//                            .replace("%t", highscore.time())
//                            .replace("%d", highscore.difficulty()));
//
//            // Player head gathering
//            ItemStack stack = item.build();
//            stack.setType(Material.PLAYER_HEAD);
//            SkullMeta meta = (SkullMeta) stack.getItemMeta();
//            if (meta == null) {
//                continue;
//            }
//            SkullSetter.setPlayerHead(Bukkit.getOfflinePlayer(uuid), meta);
//            item.meta(meta);
//
//            if (uuid.equals(player.getPlayer().getUniqueId())) {
//                menu.item(30, item.clone());
//                item.glowing();
//            }
//
//            items.add(item);
//            rank++;
//        }
//
//        menu
//                .displayRows(0, 1)
//                .addToDisplay(items)
//
//                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT) // next page
//                        .click(event -> menu.page(1)))
//
//                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
//                        .click(event -> menu.page(-1)))
//
//                .item(32, new Item(Material.ARROW, "<red>Close")
//                        .click(event -> event.getPlayer().closeInventory()))
//
//                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
//                .animation(new WaveWestAnimation())
//                .open(player.getPlayer());
//    }
//
//}
