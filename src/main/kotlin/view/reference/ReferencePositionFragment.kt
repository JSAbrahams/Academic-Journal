package main.kotlin.view.reference

import javafx.geometry.Orientation
import main.kotlin.Styles
import main.kotlin.controller.ReferencesController
import main.kotlin.model.journal.ReferencePosition
import main.kotlin.model.journal.ReferencePositionModel
import tornadofx.*

class ReferencePositionFragment : ListCellFragment<ReferencePosition>() {
    val entry = ReferencePositionModel(itemProperty)

    val referencesController: ReferencesController by inject()

    val referencesView: ZoteroView by inject()

    override val root = vbox {
        hbox {
            addClass(Styles.entryItem)
            text(entry.type)

            separator(Orientation.VERTICAL)

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
