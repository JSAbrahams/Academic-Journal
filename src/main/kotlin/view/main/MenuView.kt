package main.kotlin.view.main

import javafx.scene.control.MenuItem
import javafx.scene.control.TextInputDialog
import main.kotlin.JournalApp
import main.kotlin.controller.AppdirController
import main.kotlin.controller.JournalController
import main.kotlin.view.keyword.KeywordsView
import main.kotlin.view.reference.ZoteroView
import tornadofx.*

class MenuView : View() {
    private val journalApp: JournalApp by inject()

    private val appdirController: AppdirController by inject()
    private val journalController: JournalController by inject()

    private val zoteroView: ZoteroView by inject()
    private val keywordsView: KeywordsView by inject()
    private val openJournalView: OpenJournalView by inject()

    override val root = menubar {
        menu("File") {
            item("New").action {
                journalApp.savePrompt(currentStage?.owner)

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
                    journalApp.savePrompt(currentStage?.owner)
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

                        journalApp.savePrompt(currentStage?.owner)
                        journalController.loadJournal(journalMeta.fileProperty.value)
                    }

                    menuItem
                }
            }
            separator()
            item("Save") {
                disableWhen { journalController.journalProperty.isNull }
                action { journalApp.save() }
            }
            separator()
            item("Edit Tags") {
                disableWhen { journalController.journalProperty.isNull }
                action { keywordsView.openWindow(owner = currentStage, block = true) }
            }
        }
        menu("References") {
            item("Zotero").action {
                zoteroView.openWindow(owner = currentStage, block = true)
            }
        }
    }
}
