package main.kotlin.model.journal

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import tornadofx.ItemViewModel
import tornadofx.select
import java.io.File

@Serializable
data class JournalMeta(val title: String = "") {
    @Transient
    val titleProperty = SimpleStringProperty(title)

    @Transient
    val fileProperty = SimpleObjectProperty<File>()

    companion object {
        fun load(file: File): JournalMeta {
            val format = Json { ignoreUnknownKeys = true }
            val journalMeta: JournalMeta = format.decodeFromString(file.readText())
            journalMeta.fileProperty.set(file)
            return journalMeta
        }

        fun from(journal: Journal, file: File) = JournalMeta(journal.titleProperty.get()).also {
            it.fileProperty.set(file)
        }
    }
}

class JournalMetaModel(property: ObjectProperty<JournalMeta>) : ItemViewModel<JournalMeta>(itemProperty = property) {
    val title = bind(autocommit = true) { property.select { it.titleProperty } }
    val file = bind(autocommit = true) { property.select { it.fileProperty } }
}
