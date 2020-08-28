package main.kotlin.view.main

import javafx.collections.ObservableList
import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.model.ReferencePosition
import main.kotlin.view.reference.ReferencePositionFragment
import main.kotlin.view.reference.ZoteroView
import tornadofx.*

class ReferencesView : View() {
    val editorController: EditorController by inject()

    val zoteroView: ZoteroView by inject()

    override val root = vbox {
        addClass(Styles.customContainer)

        text("References") { setId(Styles.title) }
        listview(editorController.current.select { it.referencesProperty }) {
            vgrow = Priority.ALWAYS
            hgrow = Priority.NEVER
            cellFragment(ReferencePositionFragment::class)

            editorController.hoveredReferencePosition.onChange<ObservableList<ReferencePosition>> {
                val indices = it?.map { referencePosition -> items.indexOf(referencePosition) } ?: emptyList()
                selectionModel.clearSelection()
                if (indices.isNotEmpty()) selectionModel.selectIndices(indices.first(), *indices.toIntArray())
            }
        }

        hbox {
            addClass(Styles.buttons)
            button("+") {
                disableWhen(editorController.isEditable.not().or(editorController.selectionBounds.isNull))
                action { zoteroView.openWindow(owner = currentStage, block = true) }
            }
        }
    }
}
