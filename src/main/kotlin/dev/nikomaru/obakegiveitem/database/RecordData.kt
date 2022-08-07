package dev.nikomaru.obakegiveitem.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

object RecordData : Table() {
    val timestamp = datetime("timestamp")
    val player = varchar("player", 40)
    val recieved = bool("recieved")
}