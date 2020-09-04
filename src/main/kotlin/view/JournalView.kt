package main.kotlin.view

import javafx.scene.control.ButtonType
import javafx.stage.FileChooser
import javafx.stage.Window
import main.kotlin.controller.JournalController
import tornadofx.*

abstract class JournalView : View() {
    private val journalController: JournalController by inject()

    private val filters = arrayOf(FileChooser.ExtensionFilter("Journal Entry", "*.journal"))

    /**
     * Show save prompt which blocks owner window, with Yes, No, and Cancel options.
     * @return false if cancelled.
     */
    fun savePrompt(owner: Window?): Boolean {
        if (journalController.journalProperty.isNotNull.get() && journalController.journalProperty.selectBoolean { it.editedProperty }.value) {
            warning(
                header = "Unsaved Changes, do you wish to save first?",
                buttons = arrayOf<ButtonType>(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL),
                owner = owner,
                actionFn = {
                    when (it) {
                        ButtonType.YES -> journalController.saveJournal()
                        ButtonType.NO -> return true
                        ButtonType.CANCEL -> return false
                    }
                }
            )
        }
        return true
    }

    fun save(saveAs: Boolean = false) {
        if (saveAs || journalController.location.isNull.get()) {
            val files = chooseFile(title = "Save As", mode = FileChooserMode.Save, filters = filters)
            if (files.isNotEmpty()) journalController.saveJournal(files[0])
        } else journalController.saveJournal()
    }
}
