package main.kotlin.view.main

import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import main.kotlin.JournalApp.Companion.savePrompt
import main.kotlin.controller.AppdirController
import main.kotlin.controller.StoreController
import main.kotlin.view.reference.ZoteroView
import tornadofx.*

class MenuView : View() {
    val appdirController: AppdirController by inject()
    val storeController: StoreController by inject()

    val filters = arrayOf(FileChooser.ExtensionFilter("Journal Entry", "*.journal"))

    fun save(saveAs: Boolean = false) {
        if (saveAs || storeController.location.isNull.get()) {
            val files = chooseFile(title = "Save As", mode = FileChooserMode.Save, filters = filters)
            if (files.isNotEmpty()) storeController.saveJournal(files[0])
        } else storeController.saveJournal()
    }

    override val root = menubar {
        menu("File") {
            item("Open").action {
                if (savePrompt(storeController, currentStage?.owner)) {
                    val files = chooseFile(title = "Open", mode = FileChooserMode.Single, filters = filters)
                    if (files.isNotEmpty()) storeController.loadJournal(files[0])
                }
            }
            menu("Open Recent") {
                disableWhen(appdirController.files.isNull)
                items.bind(appdirController.files.select { it.recentFiles }.value) { path ->
                    val menuItem = MenuItem(path.toString())
                    menuItem.action {
                        if (!path.toFile().exists()) {
                            warning("Unknown file", "File no longer exists, and will be removed")
                            appdirController.files.value.recentFiles.remove(path)
                        } else if (storeController.location.isNull.get()
                            || storeController.location.isNotNull.get() && path != storeController.location.get()
                                .toPath()
                            && savePrompt(storeController, currentStage?.owner)
                        ) {
                            storeController.loadJournal(path.toFile())
                        }
                    }
                    menuItem
                }
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
        menu("References") {
            item("Zotero").action {
                ZoteroView().openWindow(owner = currentStage, block = true)
            }
        }
        menu("Help") {
            item("About")
            isVisible = false
        }
    }
}
