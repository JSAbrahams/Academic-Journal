package main.kotlin.view

import javafx.geometry.Orientation
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import tornadofx.*

class Editor : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    override val root = vbox {
        hbox {
            togglebutton("Edit Mode").bind(editorController.editMode)
            textfield(editorController.current.select { it.titleProperty }).disableWhen(editorController.editMode.not())
        }
        hbox {
            text("Creation")
            text(editorController.current.select { it.creationProperty.asString() })
            separator(Orientation.VERTICAL)
            text("Last Edit")
            text(editorController.current.select { it.lastEditProperty.asString() })
        }
        textarea(editorController.current.select { it.textProperty }).disableWhen(editorController.editMode.not())
    }
}
