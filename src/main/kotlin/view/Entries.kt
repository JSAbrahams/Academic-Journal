package main.kotlin.view

import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.fragment.EntryFragment
import tornadofx.*

class Entries : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    override val root = vbox {
        text().bind(storeController.journal.select { it.titleProperty })
        text("Entries")
        scrollpane {
            listview(storeController.journal.select { it.itemsProperty }) {
                cellFragment(EntryFragment::class)
            }
        }
        button("+").action {
            storeController.newEntry()
        }
    }
}
