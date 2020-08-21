package main.kotlin.view

import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.fragment.EntryFragment
import tornadofx.*

class Entries : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    override val root = vbox {
        addClass(Styles.container)
        hbox {
            circle(radius = 3).visibleWhen(storeController.journal.select { it.editedProperty })
            text().bind(storeController.journal.select { it.titleProperty })
        }
        text("Entries") { setId(Styles.title) }
        scrollpane {
            listview(storeController.journal.select { it.itemsProperty }) {
                cellFragment(EntryFragment::class)
            }
        }
        hbox {
            button("save") {
                disableWhen { storeController.location.isNull.or(storeController.journal.selectBoolean { it.editedProperty }) }
                action { storeController.saveJournal() }
            }
        }
        button("+").action { editorController.current.value = storeController.newEntry() }
    }
}
