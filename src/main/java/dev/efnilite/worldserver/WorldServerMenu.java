package dev.efnilite.worldserver;

import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.animation.RandomAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.SliderItem;
import dev.efnilite.vilib.inventory.item.TimedItem;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.worldserver.config.Option;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WorldServerMenu {

    public static void openMainMenu(Player player) {
        Menu main = new Menu(3, "<white>WorldServer");

        WorldPlayer wp = WorldPlayer.getPlayer(player);

        if (wp == null) {
            wp = WorldPlayer.register(player);
        }

        if (player.hasPermission("ws.spy")) {
            WorldPlayer finalWp = wp;
            main
                    .item(9, new SliderItem()
                            .initial(wp.spyMode() ? 0 : 1)
                            .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<#0DCB07><bold>Spy mode enabled")
                                    .lore("<gray>See what every player", "<gray>in every world is saying."), (event) -> {
                                finalWp.setSpyMode(true);
                                return true;
                            })
                            .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Spy mode disabled")
                                    .lore("<gray>See what every player", "<gray>in every world is saying."), (event) -> {
                                finalWp.setSpyMode(false);
                                return true;
                            }));
        }

        if (player.hasPermission("ws.option.global-chat")) {
            main
                    .item(10, new SliderItem()
                            .initial(Option.GLOBAL_CHAT_ENABLED ? 0 : 1)
                            .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<#0DCB07><bold>Global chat enabled")
                                    .lore("<gray>People can talk in global chat."), (event) -> {
                                Option.GLOBAL_CHAT_ENABLED = true;
                                WorldServer.getConfiguration().getFile("config").set("global-chat-enabled", true);
                                return true;
                            })
                            .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Global chat disabled")
                                    .lore("<gray>Global chat has been disabled."), (event) -> {
                                Option.GLOBAL_CHAT_ENABLED = false;
                                WorldServer.getConfiguration().getFile("config").set("global-chat-enabled", false);
                                return true;
                            }));
        }

        if (player.hasPermission("ws.option.chat")) {
            main
                    .item(11, new SliderItem()
                            .initial(Option.CHAT_ENABLED ? 0 : 1)
                            .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<#0DCB07><bold>Chat handling enabled")
                                    .lore("<gray>Chat is separated between worlds."), (event) -> {
                                Option.CHAT_ENABLED = true;
                                WorldServer.getConfiguration().getFile("config").set("chat-enabled", true);
                                return true;
                            })
                            .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Chat handling disabled")
                                    .lore("<gray>Chat is the same across worlds."), (event) -> {
                                Option.CHAT_ENABLED = false;
                                WorldServer.getConfiguration().getFile("config").set("chat-enabled", false);
                                return true;
                            }));
        }

        if (player.hasPermission("ws.option.tab")) {
            main
                    .item(12, new SliderItem()
                            .initial(Option.TAB_ENABLED ? 0 : 1)
                            .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<#0DCB07><bold>Tab handling enabled")
                                    .lore("<gray>Tab is separated between worlds."), (event) -> {
                                Option.TAB_ENABLED = true;
                                WorldServer.getConfiguration().getFile("config").set("tab-enabled", true);
                                return true;
                            })
                            .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Tab handling disabled")
                                    .lore("<gray>Chat is the same across worlds."), (event) -> {
                                Option.TAB_ENABLED = false;
                                WorldServer.getConfiguration().getFile("config").set("tab-enabled", false);
                                return true;
                            }));
        }

        if (player.hasPermission("ws.reload")) {
            main.item(13, new Item(Material.COMPARATOR, "&b<bold>Reload files").lore("<gray>This will reload all files.").click((event) -> {
                Menu menu = event.getMenu();
                menu.item(event.getSlot(), new TimedItem(new Item(Material.BARRIER, "<red><bold>Are you sure?")
                        .lore("<gray>If you click this item again,", "<gray>all files will be reloaded")
                        .click((event1) -> {
                    player.closeInventory();
                    Time.timerStart("reload");
                    WorldServer.getConfiguration().reload();
                    Message.send(player, WorldServer.MESSAGE_PREFIX + "Reloaded WorldServer in " + Time.timerEnd("reload") + "ms!");
                }), event, 20 * 5));
                menu.updateItem(event.getSlot());
            }));
        }

        main
                .distributeRowEvenly(1)
                .animation(new RandomAnimation())
                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .open(player);
    }

}