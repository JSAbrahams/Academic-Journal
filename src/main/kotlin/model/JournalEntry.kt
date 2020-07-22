package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.asObservable
import java.util.*

class JournalEntry(
    creation: Date,
    lastEdit: Date,
    title: String,
    text: String,
    notes: List<Note>,
    keywords: List<String>
) {
    val id = UUID.randomUUID()

    val lastEditProperty = SimpleObjectProperty(lastEdit)
    val creationProperty = SimpleObjectProperty(creation)

    val titleProperty = SimpleStringProperty(title)
    val textProperty = SimpleStringProperty(text)

    val notesProperty = SimpleListProperty<Note>(notes.asObservable())
    val keywordsProperty = SimpleListProperty<String>(keywords.asObservable())

    override fun equals(other: Any?): Boolean = id == (other as JournalEntry).id
    override fun hashCode(): Int = id.hashCode()
}

class JournalEntryModel(property: ObjectProperty<JournalEntry>) : ItemViewModel<JournalEntry>(itemProperty = property) {
    val title = bind(autocommit = true) { property.get().titleProperty }
    val text = bind(autocommit = true) { property.get().textProperty }
    val lastEdit = bind(autocommit = true) { property.get().lastEditProperty }
    val creation = bind(autocommit = true) { property.get().creationProperty }
    val notes = bind(autocommit = true) { property.get().notesProperty }
    val keywords = bind(autocommit = true) { property.get().keywordsProperty }
}
