package main.kotlin.view

import javafx.stage.FileChooser
import main.kotlin.controller.AppdirController
import main.kotlin.controller.StoreController
import tornadofx.*

class MenuView : View() {
    val storeController: StoreController by inject()
    val filters = arrayOf(FileChooser.ExtensionFilter("Journal Entry", "*.journal"))

    fun save(prompt: Boolean = false) {
        if (prompt || storeController.location.isNull.get()) {
            val files = chooseFile(title = "Save As", mode = FileChooserMode.Save, filters = filters)
            if (files.isNotEmpty()) storeController.saveJournal(files[0])
        } else storeController.saveJournal()
    }

    override val root = menubar {
        menu("File") {
            item("Open").action {
                val files = chooseFile(title = "Open", mode = FileChooserMode.Single, filters = filters)
                if (files.isNotEmpty()) storeController.loadJournal(files[0])
            }
            separator()
            item("Save") {
                disableWhen { storeController.journal.isNull }
                action { save() }
            }
            item("Save As") {
                disableWhen { storeController.journal.isNull }
                action { save(true) }
            }
            item("Export") {
                disableWhen { storeController.journal.isNull }
                isVisible = false
            }
            separator { isVisible = false }
            item("Settings") {
                isVisible = false
            }
        }
        menu("Help") {
            item("About")
            isVisible = false
        }
    }
}
