package main.kotlin.view

import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.fragment.EntryFragment
import tornadofx.*

class Entries : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    override val root = vbox {
        text("Entries")
        scrollpane {
            listview(storeController.journal.select { it.items }) {
                cellFragment(EntryFragment::class)
            }
        }
        hbox {
            button("+") {
                isVisible = false
            }
        }
    }
}
