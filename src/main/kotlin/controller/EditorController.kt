package main.kotlin.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.IndexRange
import main.kotlin.model.JournalEntry
import main.kotlin.model.ReferencePosition
import main.kotlin.model.Tag
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.onChange

class EditorController : Controller() {
    private val journalController: JournalController by inject()

    val current = SimpleObjectProperty<JournalEntry>()
    val isEditMode = SimpleBooleanProperty()
    val isEditable = current.isNotNull.and(isEditMode)

    val selectionBounds = SimpleObjectProperty<IndexRange>()
    val isValidSelection = current.isNotNull.and(selectionBounds.isNotNull)

    val rowPosition = SimpleIntegerProperty()
    val colPosition = SimpleIntegerProperty()
    val hoveredReferencePosition = SimpleListProperty(mutableListOf<ReferencePosition>().asObservable())

    val selectedKeywordProperty = SimpleObjectProperty<Tag>()

    init {
        // Set as this controller is initialized after journal is loaded
        if (journalController.journalProperty.isNotNull.get() && journalController.journalProperty.value.entriesProperty.isNotEmpty()) {
            current.set(journalController.journalProperty.value.entriesProperty.last())
        }

        journalController.journalProperty.onChange {
            if (it != null && it.entriesProperty.isNotEmpty()) current.set(it.entriesProperty.last())
        }
    }
}
