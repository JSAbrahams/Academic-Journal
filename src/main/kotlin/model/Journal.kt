package main.kotlin.model

import com.beust.klaxon.Klaxon
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.asObservable
import java.io.File

class Journal(
    title: String = "My Journal",
    items: MutableList<JournalEntry> = mutableListOf(JournalEntry())
) {
    val titleProperty = SimpleStringProperty(title)
    val itemsProperty = SimpleListProperty<JournalEntry>(items.asObservable())

    companion object {
        fun load(file: File): Journal = Klaxon().parse(file.readText()) ?: Journal()
    }

    fun save(file: File): Nothing = TODO()
}
