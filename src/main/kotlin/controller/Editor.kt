package main.kotlin.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.JournalEntry
import tornadofx.Controller

class EditorController : Controller() {
    val current = SimpleObjectProperty<JournalEntry>(JournalEntry())

    val editMode = SimpleBooleanProperty()
}
