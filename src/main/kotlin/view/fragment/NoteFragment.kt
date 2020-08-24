package main.kotlin.view.fragment

import main.kotlin.model.NoteModel
import main.kotlin.model.ReferencePosition
import tornadofx.ListCellFragment
import tornadofx.vbox

class NoteFragment : ListCellFragment<ReferencePosition>() {
    val entry = NoteModel(itemProperty)

    override val root = vbox {}
}
