package main.kotlin.view

import javafx.geometry.Orientation
import main.kotlin.controller.EditorController
import tornadofx.View
import tornadofx.hbox
import tornadofx.separator
import tornadofx.text
import tornadofx.textarea
import tornadofx.textfield
import tornadofx.vbox

class Editor : View() {
    val editorController: EditorController by inject()

    val titleField = textfield { }
    val creationDate = text { }
    val lastEditDate = text { }
    val editArea = textarea { }

    override val root = vbox {
        title
        hbox {
            text("Creation Date:")
            creationDate
            separator(Orientation.VERTICAL)
            text("Last Edit Date:")
            lastEditDate
        }
        editArea
    }
}
