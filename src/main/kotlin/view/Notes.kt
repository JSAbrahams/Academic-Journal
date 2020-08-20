package main.kotlin.view

import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.view.fragment.NoteFragment
import tornadofx.*

class Notes : View() {
    val editorController: EditorController by inject()

    override val root = vbox {
        addClass(Styles.container)
        text("References") { setId(Styles.title) }

        scrollpane {
            listview(editorController.current.select { it.notesProperty }) {
                cellFragment(NoteFragment::class)
            }
        }

        button("+")
    }
}
