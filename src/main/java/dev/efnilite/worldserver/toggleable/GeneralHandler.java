package dev.efnilite.worldserver.toggleable;

import dev.efnilite.worldserver.WorldServer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GeneralHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() && WorldServer.IS_OUTDATED) {
            BaseComponent[] message = new ComponentBuilder()
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Efnilite/WorldServer/releases/latest"))
                    .append("> ").color(ChatColor.of("#468094")).bold(true)
                    .append("Your WorldServer version is outdated. ").color(ChatColor.GRAY).bold(false)
                    .append("Click here").color(ChatColor.of("#468094")).underlined(true)
                    .append(" to visit the latest version!").color(ChatColor.GRAY).underlined(false)
                    .create();

            player.spigot().sendMessage(message);
        }
    }
}
