package main.kotlin.view

import javafx.geometry.Orientation
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import tornadofx.*

class Editor : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    override val root = vbox {
        textfield { }
        hbox {
            text("Creation Date:")
            text { }
            separator(Orientation.VERTICAL)
            text("Last Edit Date:")
            text { }
        }
        textarea { }
        button("Save") { }
    }
}
