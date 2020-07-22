package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue
import java.util.*

class JournalEntry(
    creation: Date,
    lastEdit: Date,
    title: String,
    text: String,
    references: List<Reference>,
    keywords: List<String>
) {
    val id = UUID.randomUUID()

    val lastEditProperty = SimpleObjectProperty(lastEdit)
    var lastEdit by lastEditProperty

    val creationProperty = SimpleObjectProperty(creation)
    var creation by creationProperty

    val titleProperty = SimpleStringProperty(title)
    var title: String by titleProperty

    val textProperty = SimpleStringProperty(text)
    var text by textProperty

    val referencesProperty = SimpleListProperty<Reference>()
    var references by referencesProperty

    val keywordsProperty = SimpleListProperty<String>()
    var keywords by keywordsProperty

    override fun equals(other: Any?): Boolean = this === other || (id == (other as JournalEntry).id)

    override fun hashCode(): Int = id.hashCode()
}

class JournalEntryModel(property: ObjectProperty<JournalEntry>) : ItemViewModel<JournalEntry>(itemProperty = property) {
    val title = bind(autocommit = true) { property.get().titleProperty }
    val lastEdit = bind(autocommit = true) { property.get().lastEditProperty }
    val creation = bind(autocommit = true) { property.get().creationProperty }
    val keywords = bind(autocommit = true) { property.get().keywordsProperty }
}
