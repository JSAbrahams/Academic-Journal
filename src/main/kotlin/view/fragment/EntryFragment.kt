package main.kotlin.view.fragment

import main.kotlin.controller.EditorController
import main.kotlin.model.JournalEntry
import main.kotlin.model.JournalEntryModel
import tornadofx.ListCellFragment
import tornadofx.bind
import tornadofx.circle
import tornadofx.hbox
import tornadofx.text
import tornadofx.vbox
import tornadofx.visibleWhen

class EntryFragment : ListCellFragment<JournalEntry>() {
    val editorController: EditorController by inject()
    val entry = JournalEntryModel(itemProperty)

    override val root = vbox {
        hbox {
            circle(radius = 3).visibleWhen(entry.edited)
            text().bind(entry.creation)
            text("•")
            text(entry.title)
        }
        setOnMouseClicked {
            editorController.current.value = entry.item
        }
    }
}
