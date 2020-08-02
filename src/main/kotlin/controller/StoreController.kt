package main.kotlin.controller

import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.Journal
import main.kotlin.model.JournalEntry
import tornadofx.Controller
import tornadofx.select
import java.io.File

class StoreController : Controller() {
    val location = SimpleObjectProperty<File>()
    val journal = SimpleObjectProperty<Journal>(Journal())

    fun loadJournal(file: File) {
        journal.set(Journal.load(file))
    }

    fun saveJournal() {
        journal.select { it.save() }
    }

    fun saveJournal(file: File) {
        journal.select { it.save() }
    }

    fun newEntry(): JournalEntry {
        val journalEntry = JournalEntry()
        journal.select { it.addJournalEntry(journalEntry) }
        return journalEntry
    }
}
