package main.kotlin.view

import javafx.stage.FileChooser
import main.kotlin.controller.StoreController
import tornadofx.View
import tornadofx.action
import tornadofx.chooseFile
import tornadofx.disableWhen
import tornadofx.item
import tornadofx.menu
import tornadofx.menubar
import tornadofx.separator

class Menu : View() {
    val storeController: StoreController by inject()

    override val root = menubar {
        menu("File") {
            item("Open").action {
                val file = chooseFile(
                    "Open",
                    filters = arrayOf(FileChooser.ExtensionFilter("Journal Entry", "*.journal"))
                )
                if (file.isNotEmpty()) storeController.loadJournal(file[0])
            }
            separator()
            item("Save").disableWhen { storeController.journal.isNull }
            item("Save As").disableWhen { storeController.journal.isNull }
            separator { isVisible = false }
            menu("Help") {
                item("About")
                isVisible = false
            }
        }
    }
}
