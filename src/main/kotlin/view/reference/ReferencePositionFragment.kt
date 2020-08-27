package main.kotlin.view.reference

import main.kotlin.model.NoteModel
import main.kotlin.model.ReferencePosition
import tornadofx.*

class ReferencePositionFragment : ListCellFragment<ReferencePosition>() {
    val entry = NoteModel(itemProperty)

    override val root = vbox {
        hbox {
            text(entry.start)
            text("-")
            text(entry.end)
        }

        text(entry.reference.select { it.titleProperty })
    }
}
