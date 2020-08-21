package main.kotlin.view.fragment

import main.kotlin.model.NoteModel
import main.kotlin.model.Reference
import tornadofx.ListCellFragment
import tornadofx.vbox

class NoteFragment : ListCellFragment<Reference>() {
    val entry = NoteModel(itemProperty)

    override val root = vbox {}
}
