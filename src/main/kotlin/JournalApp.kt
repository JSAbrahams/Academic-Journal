package main.kotlin

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import main.kotlin.controller.AppdirController
import main.kotlin.controller.JournalController
import main.kotlin.controller.ReferencesController
import main.kotlin.view.main.MainView
import net.harawata.appdirs.AppDirsFactory
import tornadofx.App
import tornadofx.select
import tornadofx.selectBoolean
import java.io.File

class JournalApp : App(MainView::class, Styles::class) {
    private val appdirController: AppdirController by inject()
    private val referencesController: ReferencesController by inject()
    private val journalController: JournalController by inject()

    private val mainView: MainView by inject()

    private val CREDENTIALS_APP_NAME = "AcademicJournal"
    private val CREDENTIALS_VERSION = "0.0.1"
    private val CREDENTIALS_AUTHOR = "nl.joelabrahams"

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
                val savePrompt = mainView.savePrompt(stage.owner)
                if (!savePrompt) it.consume()
            }
            appdirController.writeToFile()
        }

        stage.scene.setOnKeyPressed {
            if (it.isControlDown && it.code == KeyCode.S) mainView.save()
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java, *args)
}
