package main.kotlin.model

import com.beust.klaxon.Klaxon
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import tornadofx.asObservable
import tornadofx.cleanBind
import tornadofx.onChange
import java.io.File

class Journal(title: String = "My Journal", items: MutableList<JournalEntry> = mutableListOf()) {
    val titleProperty = SimpleStringProperty(title)
    val itemsProperty = SimpleListProperty<JournalEntry>(items.asObservable())
    var editedProperty = SimpleBooleanProperty(items.map { it.editedProperty }.any())

    init {
        itemsProperty.forEach { entry -> editedProperty.bind(entry.editedProperty) }
        itemsProperty.onChange { it: ObservableList<JournalEntry>? ->
            val or = it?.fold(
                SimpleBooleanProperty(false),
                fun(acc, b): BooleanBinding {
                    return Bindings.or(acc, b.editedProperty)
                }) ?: SimpleBooleanProperty(false)
            editedProperty.cleanBind(or)
        }
    }

    companion object {
        fun load(file: File): Journal = Klaxon().parse(file.readText()) ?: Journal()
    }

    fun save(): BooleanProperty {
        return editedProperty
    }

    fun addJournalEntry(journalEntry: JournalEntry): BooleanProperty {
        itemsProperty.add(journalEntry)
        return editedProperty
    }
}
