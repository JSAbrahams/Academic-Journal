package main.kotlin.view

import main.kotlin.controller.EditorController
import tornadofx.*

class History : View() {
    val editorController: EditorController by inject()

    val cards = vbox { }

    override val root = vbox {
        scrollpane { cards }
        hbox {
            button("+") {
                isVisible = false
            }
        }
    }
}
