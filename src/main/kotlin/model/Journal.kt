package main.kotlin.model

import com.beust.klaxon.Klaxon
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.asObservable
import tornadofx.cleanBind
import java.io.File

class Journal(title: String = "My Journal", items: MutableList<JournalEntry> = mutableListOf()) {
    val titleProperty = SimpleStringProperty(title)

    val myItems = items.asObservable()
    val itemsProperty: ReadOnlyListProperty<JournalEntry> =
        SimpleListProperty<JournalEntry>(myItems)
    var editedProperty = SimpleBooleanProperty(true)

    companion object {
        fun load(file: File): Journal = Klaxon().parse(file.readText()) ?: Journal()
    }

    fun save() {
        itemsProperty.forEach { it.editedProperty.set(false) }
    }

    fun addJournalEntry(journalEntry: JournalEntry) {
        myItems.add(journalEntry)
        val or = myItems.fold(
            SimpleBooleanProperty(false),
            fun(acc, b): BooleanBinding { return Bindings.or(acc, b.editedProperty) })

        editedProperty.cleanBind(or)
    }
}
