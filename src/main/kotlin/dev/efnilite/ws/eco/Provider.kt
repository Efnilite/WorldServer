package dev.efnilite.ws.eco

import dev.efnilite.ws.WorldPlayer
import dev.efnilite.ws.config.Config
import dev.efnilite.ws.config.Locales
import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


object Provider : AbstractEconomy() {

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
        return Config.CONFIG.getString("eco.currency-symbol") + currencyFormat.format(amount)
    }

    override fun currencyNamePlural(): String {
        return Config.CONFIG.getString("eco.currency-names.plural")
    }

    override fun currencyNameSingular(): String {
        return Config.CONFIG.getString("eco.currency-names.singular")
    }

    override fun hasAccount(s: String?): Boolean {
        return true
    }

    override fun hasAccount(s: String?, s1: String?): Boolean {
        return true
    }

    override fun getBalance(name: String): Double {
        return getPlayer(name)?.getBalance() ?: 0.0
    }

    override fun getBalance(name: String, world: String): Double {
        return getPlayer(name)?.getBalance(world) ?: 0.0
    }

    override fun has(name: String, amount: Double): Boolean {
        val player = getPlayer(name)

        return player != null && player.getBalance() >= amount
    }

    override fun has(name: String, world: String, amount: Double): Boolean {
        val player = getPlayer(name)

        return player != null && player.getBalance(world) >= amount
    }

    override fun withdrawPlayer(name: String, amount: Double): EconomyResponse {
        val player = getPlayer(name)
            ?: return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player can't be null")

        if (amount < 0) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount")
        }

        if (!Config.CONFIG.getBoolean("eco.allow-negative-balance") && amount > player.getBalance()) {
            player.player.sendMessage(Locales.getString(player.player, "eco.pay.no-funds"))
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Not enough balance")
        }
        player.alterBalance(-amount)
        return EconomyResponse(amount, player.getBalance(), EconomyResponse.ResponseType.SUCCESS, null)
    }

    override fun withdrawPlayer(name: String, world: String, amount: Double): EconomyResponse {
        val player = getPlayer(name)
            ?: return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player cant be null")

        if (amount < 0) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount")
        }

        if (!Config.CONFIG.getBoolean("eco.allow-negative-balance") && amount > player.getBalance()) {
            player.player.sendMessage(Locales.getString(player.player, "eco.pay.no-funds"))
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Not enough balance")
        }
        player.alterBalance(world, -amount)
        return EconomyResponse(
            amount,
            player.getBalance(world),
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

        player.alterBalance(amount)
        return EconomyResponse(amount, player.getBalance(), EconomyResponse.ResponseType.SUCCESS, null)
    }

    override fun depositPlayer(name: String, world: String, amount: Double): EconomyResponse {
        val player = getPlayer(name)
            ?: return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player can't be null")

        if (amount < 0) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't deposit negative amount")
        }

        player.alterBalance(world, amount)
        return EconomyResponse(
            amount,
            player.getBalance(world),
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

    private fun getPlayer(name: String): WorldPlayer? {
        return WorldPlayer.players[Bukkit.getPlayer(name)?.uniqueId]
    }
}