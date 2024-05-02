package dev.efnilite.ws.eco

import dev.efnilite.ws.WorldPlayer
import dev.efnilite.ws.config.Config
import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


class Provider : AbstractEconomy() {

    private val currencyFormat = NumberFormat.getInstance(Locale.US)

    init
    {
        currencyFormat.roundingMode = RoundingMode.FLOOR
        currencyFormat.isGroupingUsed = true
        currencyFormat.minimumFractionDigits = 2
        currencyFormat.maximumFractionDigits = 2
    }

    override fun isEnabled(): Boolean {
        return Config.CONFIG.getBoolean("eco.enabled")
    }

    override fun getName(): String {
        return "WorldServer Economy Provider"
    }

    override fun hasBankSupport(): Boolean {
        return false
    }

    override fun fractionalDigits(): Int {
        return -1
    }

    override fun format(amount: Double): String {
        return Option.ECONOMY_CURRENCY_SYMBOL + currencyFormat.format(amount)
    }

    override fun currencyNamePlural(): String {
        return Option.ECONOMY_CURRENCY_NAMES_PLURAL
    }

    override fun currencyNameSingular(): String {
        return Option.ECONOMY_CURRENCY_NAMES_SINGULAR
    }

    override fun hasAccount(s: String?): Boolean {
        return true
    }

    override fun hasAccount(s: String?, s1: String?): Boolean {
        return true
    }

    override fun getBalance(name: String): Double {
        val player = getPlayer(name)

        return if (player == null) 0 else player.getBalance()
    }

    override fun getBalance(name: String, world: String): Double {
        val player = getPlayer(name)

        return if (player == null) 0 else player.getBalance(getWorldGroup(world))
    }

    override fun has(name: String, amount: Double): Boolean {
        val player = getPlayer(name)

        return player != null && player.getBalance() >= amount
    }

    override fun has(name: String, world: String, amount: Double): Boolean {
        val player = getPlayer(name)

        return player != null && player.getBalance(getWorldGroup(world)) >= amount
    }

    override fun withdrawPlayer(name: String, amount: Double): EconomyResponse {
        val player = getPlayer(name)
            ?: return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player can't be null")

        if (amount < 0) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount")
        }

        if (!Option.ECONOMY_ALLOW_NEGATIVE_BALANCE && amount > player.getBalance()) {
            player.send(Option.ECONOMY_PAY_NO_FUNDS_FORMAT)
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Not enough balance")
        }
        player.withdraw(amount)
        return EconomyResponse(amount, player.getBalance(), EconomyResponse.ResponseType.SUCCESS, null)
    }

    override fun withdrawPlayer(name: String, world: String, amount: Double): EconomyResponse {
        val player = getPlayer(name)
            ?: return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player cant be null")

        if (amount < 0) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount")
        }

        if (!Option.ECONOMY_ALLOW_NEGATIVE_BALANCE && amount > player.getBalance()) {
            player.send(Option.ECONOMY_PAY_NO_FUNDS_FORMAT)
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Not enough balance")
        }
        player.withdraw(getWorldGroup(world), amount)
        return EconomyResponse(
            amount,
            player.getBalance(getWorldGroup(world)),
            EconomyResponse.ResponseType.SUCCESS,
            null
        )
    }

    override fun depositPlayer(name: String, amount: Double): EconomyResponse {
        val player = getPlayer(name)
            ?: return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player can't be null")

        if (amount < 0) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't deposit negative amount")
        }

        player.deposit(amount)
        return EconomyResponse(amount, player.getBalance(), EconomyResponse.ResponseType.SUCCESS, null)
    }

    override fun depositPlayer(name: String, world: String, amount: Double): EconomyResponse {
        val player = getPlayer(name)
            ?: return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player can't be null")

        if (amount < 0) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't deposit negative amount")
        }

        player.deposit(getWorldGroup(world), amount)
        return EconomyResponse(
            amount,
            player.getBalance(getWorldGroup(world)),
            EconomyResponse.ResponseType.SUCCESS,
            null
        )
    }

    override fun createBank(s: String?, s1: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "$name does not support banks")
    }

    override fun deleteBank(s: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "$name does not support banks")
    }

    override fun bankBalance(s: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "$name does not support banks")
    }

    override fun bankHas(s: String?, v: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "$name does not support banks")
    }

    override fun bankWithdraw(s: String?, v: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "$name does not support banks")
    }

    override fun bankDeposit(s: String?, v: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "$name does not support banks")
    }

    override fun isBankOwner(s: String?, s1: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "$name does not support banks")
    }

    override fun isBankMember(s: String?, s1: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "$name does not support banks")
    }

    override fun getBanks(): List<String> {
        return emptyList()
    }

    override fun createPlayerAccount(s: String?): Boolean {
        return true
    }

    override fun createPlayerAccount(name: String?, world: String?): Boolean {
        return true
    }

    private fun getWorldGroup(name: String): String {
        val world = Bukkit.getWorld(name) ?: return ""

        return GroupUtil.getGroupFromWorld(world)
    }

    private fun getPlayer(name: String): WorldPlayer? {
        val player = Bukkit.getPlayer(name) ?: return null

        return WorldPlayer.player
    }

}