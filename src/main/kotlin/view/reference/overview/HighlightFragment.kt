package main.kotlin.view.reference.overview

import javafx.beans.binding.Bindings
import main.kotlin.Styles
import main.kotlin.model.journal.ReferencePosition
import main.kotlin.model.journal.ReferencePositionModel
import tornadofx.*

class HighlightFragment : ListCellFragment<ReferencePosition>() {
    private val entry = ReferencePositionModel(itemProperty)

    override val root = vbox {
        text(entry.reference.select { it.titleProperty }) {
            addClass(Styles.entryItemTitle)
        }
        hbox {
            text("-")
            text(entry.start)
            text(":")
            text(entry.end)
        }

        text(Bindings.createStringBinding({
            if (!entry.journalEntry.isBound) return@createStringBinding ""

            val text = entry.journalEntry.value.textProperty.value
            text.substring(itemProperty.value.startProperty.get(), itemProperty.value.endProperty.get())
        }, entry.journalEntry.select { it.textProperty }))
    }
}
