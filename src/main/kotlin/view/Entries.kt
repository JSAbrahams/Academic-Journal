package main.kotlin.view

import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.fragment.EntryFragment
import tornadofx.View
import tornadofx.action
import tornadofx.bind
import tornadofx.button
import tornadofx.circle
import tornadofx.disableWhen
import tornadofx.hbox
import tornadofx.listview
import tornadofx.scrollpane
import tornadofx.select
import tornadofx.text
import tornadofx.vbox
import tornadofx.visibleWhen

class Entries : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    override val root = vbox {
        hbox {
            circle(radius = 3).visibleWhen(storeController.journal.select { it.editedProperty })
            text().bind(storeController.journal.select { it.titleProperty })
        }
        text("Entries")
        scrollpane {
            listview(storeController.journal.select { it.itemsProperty }) {
                cellFragment(EntryFragment::class)
            }
        }
        hbox {
            button("save") {
                disableWhen(storeController.journal.select { it.editedProperty.not() })
                action { storeController.saveJournal() }
            }
            button("+").action { editorController.current.value = storeController.newEntry() }
        }
    }
}
