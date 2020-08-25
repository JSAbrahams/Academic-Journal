package main.kotlin.view.reference

import main.kotlin.model.NoteModel
import main.kotlin.model.ReferencePosition
import tornadofx.*

class ReferencePositionFragment : ListCellFragment<ReferencePosition>() {
    val entry = NoteModel(itemProperty)

    override val root = vbox {
        hbox {
            text(entry.start.asString())
            text("-")
            text(entry.end.asString())
        }

        ReferenceFragment(itemProperty.select { it.reference }).root
    }
}
