package main.kotlin.view.main

import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.view.reference.ReferencePositionFragment
import tornadofx.*

class ReferencesView : View() {
    val editorController: EditorController by inject()

    override val root = vbox {
        addClass(Styles.customContainer)

        text("References") { setId(Styles.title) }
        listview(editorController.current.select { it.referencesProperty }) {
            cellFragment(ReferencePositionFragment::class)
        }

        hbox {
            addClass(Styles.buttons)
            button("+") {
                isVisible = false
            }
        }
    }
}
