package main.kotlin.view.entry

import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.model.journal.JournalEntry
import main.kotlin.model.journal.JournalEntryModel
import main.kotlin.view.tag.tagbar
import tornadofx.*

class EntryFragment : ListCellFragment<JournalEntry>() {
    val entry = JournalEntryModel(itemProperty)

    override val root = hbox {
        addClass(Styles.entryItem)

        region {
            visibleWhen(entry.edited)
            managedWhen(entry.edited)

            vgrow = Priority.ALWAYS
            val height = heightProperty()
            circle(radius = 3) { centerYProperty().bind(height.divide(2.0)) }
        }

        vbox {
            text(entry.title)
            text().bind(entry.creation)

            tagbar(entry.tags) {
                visibleWhen(entry.tags.sizeProperty.greaterThan(0))
                managedWhen(entry.tags.sizeProperty.greaterThan(0))
            }
        }
    }
}
