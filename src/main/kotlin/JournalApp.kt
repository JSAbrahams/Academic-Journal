package main.kotlin

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.*
import javafx.stage.Stage
import main.kotlin.controller.AppdirController
import javafx.stage.Window
import main.kotlin.controller.AppdirController
import main.kotlin.controller.StoreController
import net.harawata.appdirs.AppDirsFactory
import main.kotlin.view.main.MainView
import net.harawata.appdirs.AppDirsFactory
import tornadofx.App
import tornadofx.select
import tornadofx.selectBoolean
import tornadofx.warning
import java.io.File

class JournalApp : App(MainView::class, Styles::class) {
    val storeController: StoreController by inject()
    val appdirController: AppdirController by inject()

    private val CREDENTIALS_APP_NAME = "AcademicJournal"
    private val CREDENTIALS_VERSION = "0.0.1"
    private val CREDENTIALS_AUTHOR = "nl.joelabrahams"

    companion object {
        /**
         * Show save prompt which blocks owner window, with Yes, No, and Cancel options.
         *
         * @return false if cancelled. Useful if we don't wish to continue execution of consecutive actions.
         */
        fun savePrompt(storeController: StoreController, owner: Window?): Boolean {
            if (storeController.journal.isNotNull.get() && storeController.journal.selectBoolean { it.editedProperty }.value) {
                warning(
                    header = "Unsaved Changes, do you wish to save first?",
                    buttons = arrayOf<ButtonType>(YES, NO, CANCEL),
                    owner = owner,
                    actionFn = {
                        when (it) {
                            YES -> storeController.saveJournal()
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

        super.start(stage)
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
                savePrompt(storeController, stage.owner)
            }

            appdirController.writeToFile()
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java, *args)
}
