package main.kotlin.view.main

import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import main.kotlin.JournalApp.Companion.savePrompt
import main.kotlin.controller.AppdirController
import main.kotlin.controller.JournalController
import main.kotlin.view.keyword.KeywordsView
import main.kotlin.view.reference.ZoteroView
import tornadofx.*

class MenuView : View() {
    private val appdirController: AppdirController by inject()
    private val journalController: JournalController by inject()

    val zoteroView: ZoteroView by inject()
    val keywordsView: KeywordsView by inject()

    val filters = arrayOf(FileChooser.ExtensionFilter("Journal Entry", "*.journal"))

    fun save(saveAs: Boolean = false) {
        if (saveAs || journalController.location.isNull.get()) {
            val files = chooseFile(title = "Save As", mode = FileChooserMode.Save, filters = filters)
            if (files.isNotEmpty()) journalController.saveJournal(files[0])
        } else journalController.saveJournal()
    }

    override val root = menubar {
        menu("File") {
            item("Open").action {
                if (savePrompt(journalController, currentStage?.owner)) {
                    val files = chooseFile(title = "Open", mode = FileChooserMode.Single, filters = filters)
                    if (files.isNotEmpty()) journalController.loadJournal(files[0])
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
                        } else if (journalController.location.isNull.get()
                            || journalController.location.isNotNull.get() && path != journalController.location.get()
                                .toPath()
                            && savePrompt(journalController, currentStage?.owner)
                        ) {
                            journalController.loadJournal(path.toFile())
                        }
                    }
                    menuItem
                }
            }
            separator()
            item("Save") {
                disableWhen { journalController.journal.isNull }
                action { save() }
            }
            item("Save As") {
                disableWhen { journalController.journal.isNull }
                action { save(true) }
            }
            item("Export") {
                disableWhen { journalController.journal.isNull }
                isVisible = false
            }
            separator()
            item("Keywords") {
                disableWhen { journalController.journal.isNull }
                action { keywordsView.openWindow(owner = currentStage, block = true) }
            }
            separator { isVisible = false }
            item("Settings") {
                isVisible = false
            }
        }
        menu("References") {
            item("Zotero").action {
                zoteroView.openWindow(owner = currentStage, block = true)
            }
        }
        menu("Help") {
            item("About")
            isVisible = false
        }
    }
}
