package main.kotlin.view.reference

import main.kotlin.model.NoteModel
import main.kotlin.model.ReferencePosition
import tornadofx.ListCellFragment
import tornadofx.select
import tornadofx.text
import tornadofx.vbox

class ReferencePositionFragment : ListCellFragment<ReferencePosition>() {
    val entry = NoteModel(itemProperty)

    val referenceFragment: ReferenceFragment by inject()

    override val root = vbox {
        text(entry.reference.select { it.titleProperty })
    }
}
