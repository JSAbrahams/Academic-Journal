package main.kotlin.view

import main.kotlin.controller.EditorController
import main.kotlin.view.fragment.NoteFragment
import tornadofx.View
import tornadofx.button
import tornadofx.listview
import tornadofx.scrollpane
import tornadofx.select
import tornadofx.text
import tornadofx.vbox

class References : View() {
    val editorController: EditorController by inject()

    override val root = vbox {
        text("References")
        scrollpane {
            listview(editorController.current.select { it.notesProperty }) {
                cellFragment(NoteFragment::class)
            }
        }
        button("+")
    }
}
