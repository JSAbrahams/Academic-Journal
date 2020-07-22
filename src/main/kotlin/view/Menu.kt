package main.kotlin.view

import javafx.application.Platform
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.*
import javafx.stage.FileChooser
import main.kotlin.controller.StoreController
import tornadofx.*
import java.util.*

class Menu : View() {
    val storeController: StoreController by inject()

    private fun save(manual: Boolean = false) {
        if (manual || storeController.location.isNotNull.get()) {
            val file = chooseFile(
                "Open",
                filters = arrayOf(FileChooser.ExtensionFilter("Journal Entry", "*.journal"))
            )
            if (file.isNotEmpty()) {
                storeController.saveJournal(file[0])
                storeController.location.set(file[0])
            }
        } else {
            storeController.saveJournal()
        }
    }

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
            item("Save") {
                disableWhen { storeController.journal.isNull }
                action { save() }
            }
            item("Save As") {
                disableWhen { storeController.journal.isNull }
                action { save(true) }
            }
            separator()
            item("Close").action {
                if (storeController.edited.value) {
                    when (warning(
                        "Journal still open, do you wish to save first?",
                        buttons = *arrayOf<ButtonType>(OK, CANCEL, CLOSE)
                    ).showAndWait()) {
                        Optional.of(OK) -> {
                            save()
                            Platform.exit()
                        }
                        Optional.of(CLOSE) -> Platform.exit()
                    }
                } else {
                    Platform.exit()
                }
            }
            menu("Help") {
                item("About")
                isVisible = false
            }
        }
    }
}
