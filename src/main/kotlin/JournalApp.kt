package main.kotlin

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.*
import javafx.stage.Stage
import main.kotlin.controller.AppdirController
import main.kotlin.controller.StoreController
import main.kotlin.view.MainView
import net.harawata.appdirs.AppDirsFactory
import tornadofx.App
import tornadofx.select
import tornadofx.selectBoolean
import tornadofx.warning
import java.io.File

class JournalApp : App(MainView::class) {
    val storeController: StoreController by inject()
    val appdirController: AppdirController by inject()

    private val CREDENTIALS_APP_NAME = "AcademicJournal"
    private val CREDENTIALS_VERSION = "0.0.1"
    private val CREDENTIALS_AUTHOR = "nl.joelabrahams"

    override fun start(stage: Stage) {
        val appDirs = AppDirsFactory.getInstance()
        val userConfigDir = appDirs.getUserConfigDir(CREDENTIALS_APP_NAME, CREDENTIALS_VERSION, CREDENTIALS_AUTHOR)
        appdirController.appdir.set(File(userConfigDir))

        stage.titleProperty().bind(
            Bindings.concat(
                Bindings.`when`(storeController.location.isNotNull)
                    .then(Bindings.concat(" [", storeController.location.asString(), "] ")).otherwise(""),
                storeController.journal.select { it.titleProperty },
                Bindings.`when`(storeController.savedProperty).then("").otherwise(" [Unsaved]")
            )
        )

        stage.setOnCloseRequest {
            if (storeController.journal.isNotNull.get() && storeController.journal.selectBoolean { it.editedProperty }.value) {
                warning(
                    header = "Journal still open, do you wish to save first?",
                    buttons = arrayOf<ButtonType>(OK, CANCEL, CLOSE),
                    owner = stage.owner,
                    actionFn = { buttonType ->
                        when (buttonType) {
                            CANCEL -> it.consume()
                            OK -> storeController.saveJournal()
                        }
                    }
                )
            }

            appdirController.writeToFile()
        }

        super.start(stage)
    }
}

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java, *args)
}
