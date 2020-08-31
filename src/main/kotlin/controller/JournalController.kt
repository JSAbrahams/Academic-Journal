package main.kotlin.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.JournalEntry
import main.kotlin.model.journal.Journal
import tornadofx.Controller
import tornadofx.cleanBind
import tornadofx.select
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset

class JournalController : Controller() {
    val appdirController: AppdirController by inject()

    val location = SimpleObjectProperty<File>()
    val savedProperty = SimpleBooleanProperty(false)

    val journalProperty = SimpleObjectProperty(Journal())

    /**
     * Create new journal with given title and load said journal.
     */
    fun newJournal(title: String) {
        val fileName = "${LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)}.json"
        val file = appdirController.newJournal(title, fileName)
        loadJournal(file)
    }

    fun loadJournal(file: File) {
        val journal = Journal.load(file)
        journalProperty.set(journal)
        location.set(file)
        appdirController.loadedJournal(journal, file)
        savedProperty.cleanBind(journalProperty.select { it.editedProperty.not() })
    }

    fun saveJournal() = when {
        location.isNull.get() -> throw IllegalStateException("Location must be set.")
        else -> saveJournal(location.get())
    }

    /**
     * Save Journal to given location and set location.
     */
    fun saveJournal(file: File) = when {
        journalProperty.isNull.get() -> throw IllegalStateException("Journal must be be loaded.")
        else -> {
            location.set(file)
            if (journalProperty.get().titleProperty.isEmpty.get()) journalProperty.get().titleProperty.set(file.nameWithoutExtension)
            file.writeText(Json.encodeToString(journalProperty.get()))
            journalProperty.value.reset()
        }
    }

    fun newEntry(): JournalEntry {
        val journalEntry = JournalEntry()
        if (journalProperty.isNotNull.get()) {
            journalProperty.value.addJournalEntry(journalEntry)
        }
        return journalEntry
    }
}
