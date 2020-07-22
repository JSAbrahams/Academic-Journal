package main.kotlin.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.Journal
import tornadofx.Controller
import java.io.File

class StoreController : Controller() {
    val location = SimpleObjectProperty<File>()
    val edited = SimpleBooleanProperty()
    val journal = SimpleObjectProperty<Journal>()

    fun loadJournal(file: File) {
        journal.set(Journal.load(file))
    }

    fun saveJournal(): Nothing = journal.value.save(location.value)
    fun saveJournal(file: File): Nothing = journal.value.save(file)
}
