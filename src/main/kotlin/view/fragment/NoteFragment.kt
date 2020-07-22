package main.kotlin.view.fragment

import main.kotlin.model.Note
import main.kotlin.model.NoteModel
import tornadofx.ListCellFragment
import tornadofx.listview
import tornadofx.text
import tornadofx.vbox

class NoteFragment : ListCellFragment<Note>() {
    val entry = NoteModel(itemProperty)

    override val root = vbox {
        text(entry.note)
        text("References")
        listview(entry.references) {
            cellFragment(ReferenceFragment::class)
        }
    }
}
