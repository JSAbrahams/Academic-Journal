package main.kotlin.view.reference

import main.kotlin.controller.ReferencesController
import main.kotlin.model.NoteModel
import main.kotlin.model.ReferencePosition
import tornadofx.*

class ReferencePositionFragment : ListCellFragment<ReferencePosition>() {
    val entry = NoteModel(itemProperty)

    val referencesController: ReferencesController by inject()
    val referencesView: ZoteroView by inject()

    override val root = vbox {
        hbox {
            text(entry.start)
            text("-")
            text(entry.end)
        }

        text(entry.reference.select { it.titleProperty })

        setOnMouseClicked {
            if (it.clickCount == 2 && entry.reference.isNotNull.get()) {
                referencesController.selectedReference.set(entry.reference.value)
                referencesView.openWindow(owner = currentWindow, block = true)
            }
        }
    }
}
