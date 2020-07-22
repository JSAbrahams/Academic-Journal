package main.kotlin.view

import javafx.geometry.Orientation
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import tornadofx.*
import java.util.*

class Editor : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    override val root = vbox {
        textfield(editorController.current.select { it.titleProperty })
        hbox {
            text("Creation Date:")
            text(editorController.current.select { it.creationProperty.asString() })
            separator(Orientation.VERTICAL)
            text("Last Edit Date:")
            text(editorController.current.select { it.lastEditProperty.asString() })
        }
        textarea { }
        button("Save") {
            if (editorController.current.isBound) editorController.current.value.lastEditProperty.set(Calendar.getInstance().time)
        }
    }
}
