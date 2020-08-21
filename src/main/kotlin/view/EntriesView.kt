package main.kotlin.view

import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.fragment.EntryFragment
import tornadofx.*

class EntriesView : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    val menuViewView: MenuView by inject()

    override val root = vbox {
        addClass(Styles.container)
        hbox {
            circle(radius = 3).visibleWhen(storeController.journal.select { it.editedProperty })
            text().bind(storeController.journal.select { it.titleProperty })
        }
        text("Entries") { setId(Styles.title) }
        listview(storeController.journal.select { it.itemsProperty }) {
            cellFragment(EntryFragment::class)
            bindSelected(editorController.current)

            storeController.journal.onChange { _ ->
                if (items.isNotEmpty()) {
                    scrollTo(items.size - 1)
                    selectionModel.select(items.size - 1)
                }
            }
        }
        hbox {
            button("save") {
                disableWhen(storeController.savedProperty)
                action { menuViewView.save() }
            }
        }
        button("+").action { editorController.current.set(storeController.newEntry()) }
    }
}
