package main.kotlin.model

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.select
import java.util.Date
import java.util.UUID

class JournalEntry(
    creation: Date = Date(),
    lastEdit: Date = Date(),
    title: String = "",
    text: String = "",
    notes: List<Note> = listOf(),
    keywords: List<String> = listOf()
) {
    val id = UUID.randomUUID()

    val lastEditProperty = SimpleObjectProperty(lastEdit)
    val creationProperty = SimpleObjectProperty(creation)

    val editedProperty: BooleanProperty = SimpleBooleanProperty(true)
    val titleProperty = SimpleStringProperty(title)
    val textProperty = SimpleStringProperty(text)

    val notesProperty = SimpleListProperty<Note>(notes.asObservable())
    val keywordsProperty = SimpleListProperty<String>(keywords.asObservable())

    override fun equals(other: Any?): Boolean = other is JournalEntry && id == other.id
    override fun hashCode(): Int = id.hashCode()
}

class JournalEntryModel(property: ObjectProperty<JournalEntry>) :
    ItemViewModel<JournalEntry>(itemProperty = property) {
    val title = bind(autocommit = true) { property.select { it.titleProperty } }
    val text = bind(autocommit = true) { property.select { it.textProperty } }
    val edited = bind(autocommit = true) { property.select { it.editedProperty } }
    val lastEdit = bind(autocommit = true) { property.select { it.lastEditProperty } }
    val creation = bind(autocommit = true) { property.select { it.creationProperty } }
    val notes = bind(autocommit = true) { property.select { it.notesProperty } }
    val keywords = bind(autocommit = true) { property.select { it.keywordsProperty } }
}
