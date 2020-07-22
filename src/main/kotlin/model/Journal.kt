package main.kotlin.model

import com.beust.klaxon.Klaxon
import com.beust.klaxon.json
import javafx.beans.property.SimpleListProperty
import java.io.File

class Journal {
    val items = SimpleListProperty<JournalEntry>()

    companion object {
        fun load(file: File): Journal = Klaxon().parse(file.readText()) ?: Journal()
    }

    fun save(file: File) = file.writeText(json { items.toArray() }.toString())
}
