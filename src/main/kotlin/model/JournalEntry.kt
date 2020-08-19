package main.kotlin.model

import javafx.beans.property.*
import javafx.collections.ObservableList
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.onChange
import tornadofx.select
import java.util.*

class JournalEntry(
    id: UUID? = null,
    creation: Date = Date(),
    lastEdit: Date = Date(),
    title: String = "",
    text: String = "",
    keywords: List<String> = listOf(),
    notes: List<Note> = listOf()
) {
    val id = id ?: UUID.randomUUID()

    val lastEditProperty = SimpleObjectProperty(lastEdit)
    val creationProperty = SimpleObjectProperty(creation)

    val editedProperty: BooleanProperty = SimpleBooleanProperty(true)

    val titleProperty = SimpleStringProperty(title)
    val textProperty = SimpleStringProperty(text)

    val notesProperty = SimpleListProperty<Note>(notes.asObservable())
    val keywordsProperty = SimpleListProperty<String>(keywords.asObservable())

    init {
        titleProperty.onChange { editedProperty.set(true) }
        textProperty.onChange { editedProperty.set(true) }
        notesProperty.onChange<ObservableList<Note>> { editedProperty.set(true) }
        keywordsProperty.onChange<ObservableList<String>> { editedProperty.set(true) }
    }

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
