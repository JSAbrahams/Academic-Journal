package main.kotlin.view

import main.kotlin.controller.EditorController
import main.kotlin.view.fragment.NoteFragment
import tornadofx.*

class Notes : View() {
    val editorController: EditorController by inject()

    override val root = vbox {
        text("Notes")
        scrollpane {
            listview(editorController.current.select { it.notesProperty }) {
                cellFragment(NoteFragment::class)
            }
        }
    }
}
