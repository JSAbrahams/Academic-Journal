package main.kotlin.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.IndexRange
import main.kotlin.model.JournalEntry
import main.kotlin.model.ReferencePosition
import tornadofx.Controller
import tornadofx.asObservable

class EditorController : Controller() {
    val current = SimpleObjectProperty<JournalEntry>()

    val selectionBounds = SimpleObjectProperty<IndexRange>()
    val validSelection = current.isNotNull.and(selectionBounds.isNotNull)

    val hoveredReferencePosition = SimpleListProperty(mutableListOf<ReferencePosition>().asObservable())

    val editMode = SimpleBooleanProperty()
}
