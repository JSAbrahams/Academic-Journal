package main.kotlin.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.Journal
import main.kotlin.model.JournalEntry
import main.kotlin.model.reference.Reference
import tornadofx.Controller
import tornadofx.cleanBind
import tornadofx.select
import tornadofx.toObservable
import java.io.File

class StoreController : Controller() {
    val location = SimpleObjectProperty<File>()
    val savedProperty = SimpleBooleanProperty(false)

    val journal = SimpleObjectProperty(Journal())
    val references = SimpleListProperty(mutableListOf<Reference>().toObservable())
    val referenceMapping = SimpleMapProperty<Int, Reference>()

    fun loadJournal(file: File) {
        journal.set(Journal.load(file))
        location.set(file)
        savedProperty.cleanBind(journal.select { it.editedProperty.not() })
    }

    fun saveJournal() = when {
        location.isNull.get() -> throw IllegalStateException("Location must be set.")
        else -> saveJournal(location.get())
    }

    /**
     * Save Journal to given location and set location.
     */
    fun saveJournal(file: File) = when {
        journal.isNull.get() -> throw IllegalStateException("Journal must be be loaded.")
        else -> {
            location.set(file)
            if (journal.get().titleProperty.isEmpty.get()) journal.get().titleProperty.set(file.nameWithoutExtension)
            file.writeText(Json.encodeToString(journal.get()))
            journal.value.reset()
        }
    }

    fun newEntry(): JournalEntry {
        val journalEntry = JournalEntry()
        if (journal.isNotNull.get()) {
            journal.value.addJournalEntry(journalEntry)
        }
        return journalEntry
    }
}
