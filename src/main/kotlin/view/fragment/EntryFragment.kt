package main.kotlin.view.fragment

import main.kotlin.model.JournalEntry
import main.kotlin.model.JournalEntryModel
import tornadofx.*

class EntryFragment : ListCellFragment<JournalEntry>() {
    val entry = JournalEntryModel(itemProperty)

    override val root = vbox {
        hbox {
            circle(radius = 3).visibleWhen(entry.edited)
            text().bind(entry.creation)
            text("•")
            text(entry.title)
        }
    }
}
