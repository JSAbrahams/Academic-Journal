package main.kotlin.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.Journal
import main.kotlin.model.JournalEntry
import tornadofx.Controller
import java.io.File

class StoreController : Controller() {
    val location = SimpleObjectProperty<File>()
    val edited = SimpleBooleanProperty()
    val journal = SimpleObjectProperty<Journal>(Journal())

    fun loadJournal(file: File) {
        journal.set(Journal.load(file))
    }

    fun saveJournal() {
        if (location.isNotNull.get()) journal.value.save(location.value)
    }

    fun saveJournal(file: File) {
        if (journal.isNotNull.get()) journal.value.save(file)
    }

    fun newEntry(): JournalEntry {
        val journalEntry = JournalEntry()
        journal.get().itemsProperty.add(journalEntry)
        return journalEntry
    }
}
