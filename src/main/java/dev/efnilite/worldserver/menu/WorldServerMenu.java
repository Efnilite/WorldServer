package dev.efnilite.worldserver.menu;

import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.animation.SnakeSingleAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.SliderItem;
import dev.efnilite.vilib.inventory.item.TimedItem;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.vilib.util.Version;
import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.WorldServerCommand;
import dev.efnilite.worldserver.config.Config;
import dev.efnilite.worldserver.config.Option;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WorldServerMenu {

    public static void openMainMenu(Player player) {
        Menu menu = new Menu(3, "<white>WorldServer");

        WorldPlayer wp = WorldPlayer.getPlayer(player);

        if (player.hasPermission("ws.spy")) {
            menu.item(9, new SliderItem().initial(wp.spyMode ? 0 : 1).add(0, new Item(get("LIME_STAINED_GLASS_PANE", "EMERALD_BLOCK"), "<#0DCB07><bold>Spy mode enabled").lore("<gray>See what every player", "<gray>in every world is saying."), (event) -> {
                wp.spyMode = true;
                return true;
            }).add(1, new Item(get("RED_STAINED_GLASS_PANE", "REDSTONE_BLOCK"), "<red><bold>Spy mode disabled").lore("<gray>See what every player", "<gray>in every world is saying."), (event) -> {
                wp.spyMode = false;
                return true;
            }));
        }

        if (player.hasPermission("ws.option.global-chat")) {
            menu.item(10, new SliderItem().initial(Option.GLOBAL_CHAT_ENABLED ? 0 : 1).add(0, new Item(get("LIME_STAINED_GLASS_PANE", "EMERALD_BLOCK"), "<#0DCB07><bold>Global chat enabled").lore("<gray>People can talk in global chat."), (event) -> {
                Option.GLOBAL_CHAT_ENABLED = true;
                Config.CONFIG.set("global-chat-enabled", true);
                return true;
            }).add(1, new Item(get("RED_STAINED_GLASS_PANE", "REDSTONE_BLOCK"), "<red><bold>Global chat disabled").lore("<gray>Global chat has been disabled."), (event) -> {
                Option.GLOBAL_CHAT_ENABLED = false;
                Config.CONFIG.set("global-chat-enabled", false);
                return true;
            }));
        }

        if (player.hasPermission("ws.option.chat")) {
            menu.item(11, new SliderItem().initial(Option.CHAT_ENABLED ? 0 : 1).add(0, new Item(get("LIME_STAINED_GLASS_PANE", "EMERALD_BLOCK"), "<#0DCB07><bold>Chat handling enabled").lore("<gray>Chat is separated between worlds."), (event) -> {
                Option.CHAT_ENABLED = true;
                Config.CONFIG.set("chat-enabled", true);
                return true;
            }).add(1, new Item(get("RED_STAINED_GLASS_PANE", "REDSTONE_BLOCK"), "<red><bold>Chat handling disabled").lore("<gray>Chat is the same across worlds."), (event) -> {
                Option.CHAT_ENABLED = false;
                Config.CONFIG.set("chat-enabled", false);
                return true;
            }));
        }

        if (player.hasPermission("ws.option.tab")) {
            menu.item(12, new SliderItem().initial(Option.TAB_ENABLED ? 0 : 1).add(0, new Item(get("LIME_STAINED_GLASS_PANE", "EMERALD_BLOCK"), "<#0DCB07><bold>Tab handling enabled").lore("<gray>Tab is separated between worlds."), (event) -> {
                Option.TAB_ENABLED = true;
                Config.CONFIG.set("tab-enabled", true);
                return true;
            }).add(1, new Item(get("RED_STAINED_GLASS_PANE", "REDSTONE_BLOCK"), "<red><bold>Tab handling disabled").lore("<gray>Chat is the same across worlds."), (event) -> {
                Option.TAB_ENABLED = false;
                Config.CONFIG.set("tab-enabled", false);
                return true;
            }));
        }

        if (player.hasPermission("ws.reload")) {
            menu.item(13, new Item(Material.HOPPER, "<blue><bold>Reload files").lore("<gray>This will reload all files.").click((event) -> {
                Menu cmenu = event.menu();
                cmenu.item(event.slot(), new TimedItem(new Item(Material.BARRIER, "<red><bold>Are you sure?").lore("<gray>If you click this item again,", "<gray>all files will be reloaded").click((event1) -> {
                    player.closeInventory();
                    Time.timerStart("reload");
                    Config.reload();
                    WorldServerCommand.send(player, WorldServer.MESSAGE_PREFIX + "Reloaded WorldServer in " + Time.timerEnd("reload") + "ms!");
                }), event).stay(20 * 5));
                cmenu.updateItem(event.slot());
            }));
        }

        menu.distributeRowEvenly(1).animation(new SnakeSingleAnimation()).fillBackground(get("GRAY_STAINED_GLASS_PANE", "AIR")).open(player);
    }

    public static Material get(String updated, String old) {
        return Version.isHigherOrEqual(Version.V1_13) ? Material.valueOf(updated) : Material.valueOf(old);
    }
}