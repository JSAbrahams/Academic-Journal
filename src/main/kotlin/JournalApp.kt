package main.kotlin

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.*
import javafx.stage.Stage
import javafx.stage.Window
import main.kotlin.controller.AppdirController
import main.kotlin.controller.JournalController
import main.kotlin.controller.ReferencesController
import main.kotlin.view.main.MainView
import net.harawata.appdirs.AppDirsFactory
import tornadofx.App
import tornadofx.select
import tornadofx.selectBoolean
import tornadofx.warning
import java.io.File

class JournalApp : App(MainView::class, Styles::class) {
    val journalController: JournalController by inject()
    val appdirController: AppdirController by inject()
    val referencesController: ReferencesController by inject()

    private val CREDENTIALS_APP_NAME = "AcademicJournal"
    private val CREDENTIALS_VERSION = "0.0.1"
    private val CREDENTIALS_AUTHOR = "nl.joelabrahams"

    companion object {
        /**
         * Show save prompt which blocks owner window, with Yes, No, and Cancel options.
         *
         * @return false if cancelled. Useful if we don't wish to continue execution of consecutive actions.
         */
        fun savePrompt(journalController: JournalController, owner: Window?): Boolean {
            if (journalController.journal.isNotNull.get() && journalController.journal.selectBoolean { it.editedProperty }.value) {
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
//                Bindings.`when`(journalController.location.isNotNull)
//                    .then(Bindings.concat(" [", journalController.location.asString(), "] ")).otherwise(""),
                journalController.journal.select { it.titleProperty },
//                Bindings.`when`(SimpleBooleanProperty(true)).then("").otherwise(" [Unsaved]")
            )
        )

        stage.setOnCloseRequest {
            if (journalController.journal.isNotNull.get() && journalController.journal.selectBoolean { it.editedProperty }.value) {
                val savePrompt = savePrompt(journalController, stage.owner)
                if (!savePrompt) it.consume()
            }
            appdirController.writeToFile()
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java, *args)
}
