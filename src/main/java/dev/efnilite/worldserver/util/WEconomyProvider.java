package dev.efnilite.worldserver.util;

import dev.efnilite.worldserver.WorldPlayer;
import dev.efnilite.worldserver.config.Option;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class WEconomyProvider extends AbstractEconomy {

    @Override
    public boolean isEnabled() {
        return Option.ECONOMY_ENABLED;
    }

    @Override
    public String getName() {
        return "WorldServer Economy Provider";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return Option.ECONOMY_CURRENCY_SYMBOL + Util.CURRENCY_FORMAT.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return Option.ECONOMY_CURRENCY_NAMES.get(1);
    }

    @Override
    public String currencyNameSingular() {
        return Option.ECONOMY_CURRENCY_NAMES.get(0);
    }

    @Override
    public boolean hasAccount(String s) {
        return true;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return true;
    }

    @Override
    public double getBalance(String name) {
        WorldPlayer player = getPlayer(name);

        return player == null ? 0 : player.getBalance();
    }

    @Override
    public double getBalance(String name, String world) {
        WorldPlayer player = getPlayer(name);

        return player == null ? 0 : player.getBalance(getWorldGroup(world));
    }

    @Override
    public boolean has(String name, double amount) {
        WorldPlayer player = getPlayer(name);

        return player != null && player.getBalance() >= amount;
    }

    @Override
    public boolean has(String name, String world, double amount) {
        WorldPlayer player = getPlayer(name);

        return player != null && player.getBalance(getWorldGroup(world)) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, double amount) {
        WorldPlayer player = getPlayer(name);

        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player can't be null");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount");
        }

        player.withdraw(amount);
        return new EconomyResponse(amount, player.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, String world, double amount) {
        WorldPlayer player = getPlayer(name);

        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player cant be null");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount");
        }

        player.withdraw(getWorldGroup(world), amount);
        return new EconomyResponse(amount, player.getBalance(getWorldGroup(world)), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String name, double amount) {
        WorldPlayer player = getPlayer(name);

        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player can't be null");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Can't deposit negative amount");
        }

        player.deposit(amount);
        return new EconomyResponse(amount, player.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String name, String world, double amount) {
        WorldPlayer player = getPlayer(name);

        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player can't be null");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Can't deposit negative amount");
        }

        player.deposit(getWorldGroup(world), amount);
        return new EconomyResponse(amount, player.getBalance(getWorldGroup(world)), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support banks");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support banks");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support banks");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support banks");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support banks");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support banks");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support banks");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support banks");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String name, String world) {
        return true;
    }

    private String getWorldGroup(String name) {
        World world = Bukkit.getWorld(name);

        if (world == null) {
            return "";
        }

        return Option.getGroupFromWorld(world);
    }

    private WorldPlayer getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);

        if (player == null) {
            return null;
        }

        return WorldPlayer.getPlayer(player);
    }
}
