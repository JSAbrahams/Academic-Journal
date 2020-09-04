package main.kotlin.view.fragment

import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.model.JournalEntry
import main.kotlin.model.JournalEntryModel
import main.kotlin.view.tag.tagbar
import tornadofx.*

class EntryFragment : ListCellFragment<JournalEntry>() {
    val entry = JournalEntryModel(itemProperty)

    override val root = hbox {
        region {
            vgrow = Priority.ALWAYS
            val height = heightProperty()
            circle(radius = 3) {
                centerYProperty().bind(height.divide(2.0))
                visibleWhen(entry.edited)
                managedWhen(entry.edited)
            }
        }

        vbox {
            hbox {
                addClass(Styles.entryItem)
                text().bind(entry.creation)
                text("•")
                text(entry.title)
            }

            hbox {
                addClass(Styles.entryItem)
                visibleWhen(entry.tags.sizeProperty.greaterThan(0))
                managedWhen(entry.tags.sizeProperty.greaterThan(0))

                text("Tags")
                tagbar(entry.tags)
            }
        }
    }
}
