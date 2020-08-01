package main.kotlin.view

import javafx.geometry.Orientation
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import tornadofx.View
import tornadofx.disableWhen
import tornadofx.hbox
import tornadofx.select
import tornadofx.separator
import tornadofx.text
import tornadofx.textarea
import tornadofx.textfield
import tornadofx.togglebutton
import tornadofx.vbox

class Editor : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    override val root = vbox {
        hbox {
            textfield(editorController.current.select { it.titleProperty }).disableWhen(
                editorController.editMode.not()
            )
        }
        hbox {
            text("Creation")
            text(editorController.current.select { it.creationProperty.asString() })
            separator(Orientation.VERTICAL)
        }
        textarea(editorController.current.select { it.textProperty }) {
            disableWhen(editorController.editMode.not())
        }
        togglebutton("Edit Mode") {
            editorController.editMode.bind(this.selectedProperty())
        }
    }
}
