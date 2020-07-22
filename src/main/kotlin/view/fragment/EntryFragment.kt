package main.kotlin.view.fragment

import javafx.geometry.Orientation
import main.kotlin.controller.EditorController
import main.kotlin.model.JournalEntry
import main.kotlin.model.JournalEntryModel
import tornadofx.*

class EntryFragment : ListCellFragment<JournalEntry>() {
    val editorController: EditorController by inject()
    val entry = JournalEntryModel(itemProperty)

    override val root = gridpane {
        text(entry.title)
        hbox {
            text(entry.creation.toString())
            separator(Orientation.VERTICAL)
            text(entry.lastEdit.toString())
        }
        listview(entry.keywords) { }
    }
}
