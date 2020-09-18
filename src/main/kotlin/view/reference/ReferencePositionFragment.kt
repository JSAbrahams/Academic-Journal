package main.kotlin.view.reference

import javafx.geometry.Orientation
import javafx.scene.input.MouseButton
import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.OverviewController
import main.kotlin.controller.ReferencesController
import main.kotlin.model.journal.ReferencePosition
import main.kotlin.model.journal.ReferencePositionModel
import main.kotlin.view.reference.overview.OverviewView
import tornadofx.*

class ReferencePositionFragment : ListCellFragment<ReferencePosition>() {
    private val entry = ReferencePositionModel(itemProperty)

    private val overviewController: OverviewController by inject()
    private val referencesController: ReferencesController by inject()
    private val editorController: EditorController by inject()

    private val referencesView: ZoteroView by inject()
    private val overviewView: OverviewView by inject()

    override val root = hbox {
        addClass(Styles.entryItem)

        button("Overview") {
            visibleWhen(entry.reference.isNotNull)
            managedWhen(entry.reference.isNotNull)
            fitToParentHeight()

            action {
                overviewController.currentReference.set(entry.reference.value)
                overviewView.openWindow(owner = currentStage, block = true)
            }
        }

        vbox {
            hbox {
                addClass(Styles.entryItem)
                text(entry.type)

                separator(Orientation.VERTICAL)

                text(entry.start)
                text("-")
                text(entry.end)
            }

            text(entry.reference.select { it.titleProperty })

        }

        setOnMouseClicked {
            if (entry.reference.isNull.get()) return@setOnMouseClicked

            if (it.button == MouseButton.PRIMARY && it.clickCount == 2) {
                referencesController.selectedReferenceProperty.set(entry.reference.value)
                referencesView.openWindow(owner = currentWindow, block = true)
            } else if (it.button == MouseButton.SECONDARY && it.clickCount == 2 && editorController.current.isNotNull.get()) {
                editorController.current.value.referencesProperty.remove(itemProperty.value)
            }
        }
    }
}
