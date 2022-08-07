package dev.nikomaru.obakegiveitem.commands

import cloud.commandframework.annotations.*
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import com.noticemc.noticeitemapi.utils.Utils.Companion.decode
import dev.nikomaru.obakegiveitem.Config
import dev.nikomaru.obakegiveitem.ObakeGiveItem
import dev.nikomaru.obakegiveitem.ObakeGiveItem.Companion.plugin
import dev.nikomaru.obakegiveitem.database.RecordData
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectOutputStream
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.io.ByteArrayOutputStream
import java.util.*

class SetItemCommand {
    @CommandMethod("ogi setitem <file>")
    @CommandPermission("obakegiveitem.setitem")
    fun setItem(sender: CommandSender, @Argument(value = "file") fileName: String) {
        if (sender !is Player) {
            sender.sendMessage("このコマンドはプレイヤーのみ実行できます")
            return
        }
        val item = sender.inventory.itemInMainHand
        val string = item.encode()
        val file = plugin.dataFolder.resolve("data").resolve("$fileName.dat")
        file.parentFile.mkdirs()
        file.createNewFile()
        file.writeText(string)
        sender.sendMessage("${file.name}に${item.displayName().toPlainText()}保存しました")
    }

    @CommandMethod("ogi getitem")
    @CommandPermission("obakegiveitem.getitem")
    suspend fun giveItem(sender: CommandSender) {

        val list = arrayListOf<UUID>()

        newSuspendedTransaction(Dispatchers.IO) {
            RecordData.select { RecordData.recieved eq false }.forEach { data ->
                list.add(data[RecordData.player].toUUID())
                RecordData.update({ RecordData.player eq data[RecordData.player] }) {
                    it[recieved] = true
                }
            }
        }

        val files = Config.config.fileName.map { plugin.dataFolder.resolve("data").resolve("${it}.dat") }
        val data = files.map { it.readText().decode() } as ArrayList

        list.stream().forEach { uuid ->
            val itemName = data.joinToString(", ") { it.displayName().toPlainText() }
            val ulid = ObakeGiveItem.nia.addItem(uuid.toOfflinePlayer(), data, null, null, Config.config.description)
            sender.sendMessage("ULID : $ulid  ${uuid.toOfflinePlayer().name}に${itemName}を送りました")
        }
    }

    @CommandMethod("ogi test <playerName>")
    @CommandPermission("obakegiveitem.test")
    fun test(sender: CommandSender, @Argument(value = "playerName", suggestions = "playerName") playerName: String) {
        val player = Bukkit.getOfflinePlayerIfCached(playerName) ?: return
        val files = Config.config.fileName.map { plugin.dataFolder.resolve("data").resolve("${it}.dat") }
        val data = files.map { it.readText().decode() } as ArrayList

        val itemName = data.joinToString(", ") { it.displayName().toPlainText() }
        val ulid = ObakeGiveItem.nia.addItem(player, data, null, null, Config.config.description)
        sender.sendMessage("ULID : $ulid  ${player.name}に${itemName}を送りました")
    }

    @Suggestions("playerName")
    fun playerNameSuggestions(sender: CommandContext<CommandSender>, input: String?): List<String> {
        return Bukkit.getServer().onlinePlayers.map { it.name }
    }

    private fun String.toUUID(): UUID {
        return UUID.fromString(this)
    }

    private fun UUID.toOfflinePlayer(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(this)
    }

    private fun ItemStack.encode(): String {
        val baos = ByteArrayOutputStream()
        val boos = BukkitObjectOutputStream(baos)
        boos.writeObject(this)
        boos.flush()
        val serializedObject = baos.toByteArray()
        return Base64.getEncoder().encodeToString(serializedObject)
    }

    private fun Component.toPlainText(): String {
        return PlainTextComponentSerializer.plainText().serialize(this)
    }
}