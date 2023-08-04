package dev.efnilite.worldserver.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * A custom command wrapper
 */
public class BukkitCommand extends Command {

    private final CommandExecutor executor;

    public BukkitCommand(String label, CommandExecutor executor) {
        super(label);
        this.executor = executor;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        executor.onCommand(sender, this, label, args);
        return true;
    }
}