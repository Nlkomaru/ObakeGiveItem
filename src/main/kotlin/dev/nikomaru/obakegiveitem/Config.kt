package dev.nikomaru.obakegiveitem

import dev.nikomaru.obakegiveitem.ObakeGiveItem.Companion.plugin
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

object Config {
    lateinit var config: ConfigData

    fun load() {
        val file = plugin.dataFolder.resolve("config.json")
        val json = Json {
            isLenient = true
            prettyPrint = true
        }

        val database = DatabaseConfig()
        val configData = json.encodeToString(ConfigData(database, arrayListOf(), "報酬です"))

        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText(configData)
        }
        config = json.decodeFromString(file.readText())
    }
}

@Serializable
data class ConfigData(val database: DatabaseConfig, val fileName: ArrayList<String>, val description: String)

@Serializable
data class DatabaseConfig(val name: String = "database",
    val port: Int = 3306,
    val user: String = "root",
    val password: String = "pass",
    val host: String = "localhost")