package main.kotlin.controller

import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.Journal
import main.kotlin.model.JournalEntry
import tornadofx.Controller
import java.io.File

class StoreController : Controller() {
    val location = SimpleObjectProperty<File>()
    val journal = SimpleObjectProperty(Journal())

    fun loadJournal(file: File) {
        journal.set(Journal.load(file))
    }

    fun saveJournal() {
        if (journal.isNotNull.get()) {
            journal.value.reset()
        }
    }

    fun saveJournal(file: File) {
        if (journal.isNotNull.get()) {
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
