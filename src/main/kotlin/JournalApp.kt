package main.kotlin

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.*
import javafx.scene.input.KeyCode
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.Window
import main.kotlin.controller.AppdirController
import main.kotlin.controller.JournalController
import main.kotlin.controller.ReferencesController
import main.kotlin.view.main.MainView
import net.harawata.appdirs.AppDirsFactory
import tornadofx.*
import java.io.File

class JournalApp : App(MainView::class, Styles::class) {
    val journalController: JournalController by inject()
    val appdirController: AppdirController by inject()
    val referencesController: ReferencesController by inject()

    val filters = arrayOf(FileChooser.ExtensionFilter("Journal Entry", "*.journal"))

    private val CREDENTIALS_APP_NAME = "AcademicJournal"
    private val CREDENTIALS_VERSION = "0.0.1"
    private val CREDENTIALS_AUTHOR = "nl.joelabrahams"

    /**
     * Show save prompt which blocks owner window, with Yes, No, and Cancel options.
     * @return false if cancelled.
     */
    fun savePrompt(owner: Window?): Boolean {
        if (journalController.journalProperty.isNotNull.get() && journalController.journalProperty.selectBoolean { it.editedProperty }.value) {
            warning(
                header = "Unsaved Changes, do you wish to save first?",
                buttons = arrayOf<ButtonType>(YES, NO, CANCEL),
                owner = owner,
                actionFn = {
                    when (it) {
                        YES -> journalController.saveJournal()
                        NO -> return true
                        CANCEL -> return false
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

    override fun start(stage: Stage) {
        val appDirs = AppDirsFactory.getInstance()
        val userConfigDir = appDirs.getUserConfigDir(CREDENTIALS_APP_NAME, CREDENTIALS_VERSION, CREDENTIALS_AUTHOR)
        appdirController.appdir.set(File(userConfigDir))

        Platform.runLater {
            try {
                referencesController.connect()
                referencesController.refreshReferences()
            } catch (e: Exception) {
                // TODO use logger
                e.printStackTrace()
            }
        }

        super.start(stage)
        stage.titleProperty().bind(
            Bindings.concat(
                journalController.journalProperty.select { it.titleProperty },
                Bindings.`when`(journalController.journalProperty.selectBoolean { it.editedProperty })
                    .then(" [Unsaved]").otherwise("")
            )
        )

        stage.setOnCloseRequest {
            if (journalController.journalProperty.isNotNull.get() && journalController.journalProperty.selectBoolean { it.editedProperty }.value) {
                val savePrompt = savePrompt(stage.owner)
                if (!savePrompt) it.consume()
            }
            appdirController.writeToFile()
        }

        stage.scene.setOnKeyPressed {
            if (it.isControlDown && it.code == KeyCode.S) save()
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java, *args)
}
