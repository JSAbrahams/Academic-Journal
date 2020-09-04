package main.kotlin.view.main

import javafx.scene.control.MenuItem
import javafx.scene.control.TextInputDialog
import main.kotlin.controller.AppdirController
import main.kotlin.controller.JournalController
import main.kotlin.view.JournalView
import main.kotlin.view.reference.ZoteroView
import main.kotlin.view.tag.TagView
import tornadofx.*

class MenuView : JournalView() {
    private val appdirController: AppdirController by inject()
    private val journalController: JournalController by inject()

    private val zoteroView: ZoteroView by inject()
    private val tagView: TagView by inject()
    private val openJournalView: OpenJournalView by inject()

    override val root = menubar {
        menu("File") {
            item("New").action {
                savePrompt(currentStage?.owner)

                val title = TextInputDialog().also {
                    it.title = "Create New Journal"
                    it.headerText = "Please Give the New Journal A Name"
                    it.contentText = "Name"
                }.showAndWait()

                title.ifPresent { journalController.newJournal(it) }
            }
            item("Open") {
                journalController.journalProperty.onChange { openJournalView.close() }
                action {
                    savePrompt(currentStage?.owner)
                    openJournalView.openWindow(owner = currentStage, block = true)
                }
            }
            menu("Open Recent") {
                disableWhen(appdirController.recentJournals.sizeProperty().isEqualTo(0))
                items.bind(appdirController.recentJournals) { journalMeta ->
                    val menuItem = MenuItem(journalMeta.title)
                    menuItem.disableWhen(journalController.location.isEqualTo(journalMeta.fileProperty))

                    menuItem.action {
                        if (journalMeta.fileProperty.isNull.get() || !journalMeta.fileProperty.get().exists()) {
                            warning("Unknown file", "File no longer exists, and will be removed")
                            appdirController.recentJournals.remove(journalMeta)
                            return@action
                        }

                        savePrompt(currentStage?.owner)
                        journalController.loadJournal(journalMeta.fileProperty.value)
                    }

                    menuItem
                }
            }
            separator()
            item("Save") {
                disableWhen { journalController.journalProperty.isNull }
                action { save() }
            }
            separator()
            item("Edit Tags") {
                disableWhen { journalController.journalProperty.isNull }
                action { tagView.openWindow(owner = currentStage, block = true) }
            }
        }
        menu("References") {
            item("Zotero").action {
                zoteroView.openWindow(owner = currentStage, block = true)
            }
        }
    }
}
