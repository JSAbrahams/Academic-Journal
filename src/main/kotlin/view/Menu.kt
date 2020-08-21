package main.kotlin.view

import javafx.stage.FileChooser
import main.kotlin.controller.StoreController
import tornadofx.*

class Menu : View() {
    val storeController: StoreController by inject()
    val filters = arrayOf(FileChooser.ExtensionFilter("Journal Entry", "*.journal"))

    override val root = menubar {
        menu("File") {
            item("Open").action {
                val files = chooseFile(title = "Open", mode = FileChooserMode.Single, filters = filters)
                if (files.isNotEmpty()) storeController.loadJournal(files[0])
            }
            separator()
            item("Save") {
                disableWhen { storeController.journal.isNull }
                action {
                    if (storeController.location.isNull.get()) {
                        val files = chooseFile(title = "Save As", mode = FileChooserMode.Save, filters = filters)
                        if (files.isNotEmpty()) storeController.saveJournal(files[0])
                    } else storeController.saveJournal()
                }
            }
            item("Save As") {
                disableWhen { storeController.journal.isNull }
                action {
                    val files = chooseFile(title = "Save As", mode = FileChooserMode.Save, filters = filters)
                    if (files.isNotEmpty()) storeController.saveJournal(files[0])
                }
            }
            item("Export").disableWhen { storeController.journal.isNull }
            separator()
            item("Settings")
        }
        menu("Help") {
            item("About")
            isVisible = false
        }
    }
}
