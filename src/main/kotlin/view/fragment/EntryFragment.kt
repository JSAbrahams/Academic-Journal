package main.kotlin.view.fragment

import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.model.JournalEntry
import main.kotlin.model.JournalEntryModel
import main.kotlin.view.keyword.KeywordFragment
import tornadofx.*

class EntryFragment : ListCellFragment<JournalEntry>() {
    val entry = JournalEntryModel(itemProperty)

    override val root = vbox {
        hbox {
            addClass(Styles.entryItem)
            region {
                vgrow = Priority.ALWAYS
                val height = heightProperty()
                circle(radius = 3) {
                    centerYProperty().bind(height.divide(2.0))
                    visibleWhen(entry.edited)
                    managedWhen(entry.edited)
                }
            }

            text().bind(entry.creation)
            text("•")
            text(entry.title)
        }

        hbox {
            addClass(Styles.entryItem)
            visibleWhen(entry.keywords.sizeProperty.greaterThan(0))
            managedWhen(entry.keywords.sizeProperty.greaterThan(0))

            text("Keywords")
            listview(entry.keywords) {
                addClass(Styles.keywords)
                cellFragment(KeywordFragment::class)
            }
        }
    }
}
