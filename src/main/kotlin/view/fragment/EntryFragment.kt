package main.kotlin.view.fragment

import main.kotlin.controller.EditorController
import main.kotlin.model.JournalEntry
import main.kotlin.model.JournalEntryModel
import tornadofx.*

class EntryFragment : ListCellFragment<JournalEntry>() {
    val editorController: EditorController by inject()
    val entry = JournalEntryModel(itemProperty)

    override val root = vbox {
        hbox {
            text().bind(entry.creation)
            text(entry.title)
        }
//        listview(entry.keywords) { }
    }
}
