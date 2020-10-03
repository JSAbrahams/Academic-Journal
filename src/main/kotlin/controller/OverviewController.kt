package main.kotlin.controller

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.journal.ReferencePosition
import main.kotlin.model.journal.ReferenceType
import main.kotlin.model.reference.Reference
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.onChange

class OverviewController : Controller() {
    private val journalController: JournalController by inject()

    val currentReference = SimpleObjectProperty<Reference>()

    private val highlights = mutableListOf<ReferencePosition>().asObservable()
    val highlightsProperty: ReadOnlyListProperty<ReferencePosition> = SimpleListProperty(highlights)
    private val summary = mutableListOf<ReferencePosition>().asObservable()
    val summaryProperty: ReadOnlyListProperty<ReferencePosition> = SimpleListProperty(summary)

    init {
        currentReference.onChange {
            highlights.clear()
            summary.clear()

            if (journalController.journalProperty.isNull.get() || currentReference.isNull.get()) return@onChange

            val journal = journalController.journalProperty.get()
            val referencePositions = journal
                .entriesProperty
                .flatMap { it.referencesProperty }
                .filter { it.referenceId == currentReference.get().id }

            highlights.addAll(referencePositions.filter { it.typeProperty.get() == ReferenceType.HIGHLIGHT })
            summary.addAll(referencePositions.filter { it.typeProperty.get() == ReferenceType.SUMMARY })
        }
    }
}
