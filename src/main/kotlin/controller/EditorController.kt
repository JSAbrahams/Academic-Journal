package main.kotlin.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.IndexRange
import main.kotlin.model.JournalEntry
import main.kotlin.model.Keyword
import main.kotlin.model.ReferencePosition
import tornadofx.Controller
import tornadofx.asObservable

class EditorController : Controller() {
    val current = SimpleObjectProperty<JournalEntry>()
    val isEditMode = SimpleBooleanProperty()
    val isEditable = current.isNotNull.and(isEditMode)

    val selectionBounds = SimpleObjectProperty<IndexRange>()
    val isValidSelection = current.isNotNull.and(selectionBounds.isNotNull)

    val rowPosition = SimpleIntegerProperty()
    val colPosition = SimpleIntegerProperty()
    val hoveredReferencePosition = SimpleListProperty(mutableListOf<ReferencePosition>().asObservable())

    val selectedKeywordProperty = SimpleObjectProperty<Keyword>()
}
