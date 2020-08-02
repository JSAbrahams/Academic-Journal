package main.kotlin.model

import com.beust.klaxon.Klaxon
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import tornadofx.asObservable
import tornadofx.onChange
import tornadofx.toBinding
import java.io.File

class Journal(title: String = "My Journal", items: MutableList<JournalEntry> = mutableListOf()) {
    val titleProperty = SimpleStringProperty(title)
    val itemsProperty = SimpleListProperty<JournalEntry>(items.asObservable())
    var editedProperty: BooleanBinding = SimpleBooleanProperty(true).toBinding()

    init {
        itemsProperty.forEach { entry -> editedProperty = editedProperty.or(entry.editedProperty) }
        itemsProperty.onChange { it: ObservableList<JournalEntry>? ->
            editedProperty = SimpleBooleanProperty(false).toBinding()
            it?.forEach { editedProperty = editedProperty.or(it.editedProperty) }
        }
    }

    companion object {
        fun load(file: File): Journal = Klaxon().parse(file.readText()) ?: Journal()
    }

    fun save(file: File): Nothing = TODO()
}
