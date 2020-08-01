package main.kotlin.view

import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.fragment.EntryFragment
import tornadofx.View
import tornadofx.action
import tornadofx.bind
import tornadofx.button
import tornadofx.listview
import tornadofx.scrollpane
import tornadofx.select
import tornadofx.text
import tornadofx.vbox

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
            val journalEntry = storeController.newEntry()
            editorController.current.value = journalEntry
        }
    }
}
