package main.kotlin

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.CLOSE
import javafx.scene.control.ButtonType.OK
import javafx.stage.Stage
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.Main
import tornadofx.App
import tornadofx.select
import tornadofx.selectBoolean
import tornadofx.warning

class JournalApp : App(Main::class) {
    val storeController: StoreController by inject()
    val editorController: EditorController by inject()

    override fun start(primaryStage: Stage) {
        super.start(primaryStage)
        primaryStage.titleProperty().bind(
            Bindings.concat(
                Bindings.`when`(storeController.journal.selectBoolean { it.editedProperty })
                    .then("[Unsaved] ").otherwise(""),
                storeController.journal.select { it.titleProperty },
                Bindings.`when`(storeController.location.isNotNull)
                    .then(Bindings.concat(" [", storeController.location.get(), "]")).otherwise("")
            )
        )

        primaryStage.setOnCloseRequest {
            if (storeController.journal.isNotNull.get() && storeController.journal.selectBoolean { it.editedProperty }.value) {
                warning(
                    header = "Journal still open, do you wish to save first?",
                    buttons = *arrayOf<ButtonType>(OK, CANCEL, CLOSE),
                    owner = primaryStage.owner,
                    actionFn = { buttonType ->
                        when (buttonType) {
                            CANCEL -> it.consume()
                            OK -> storeController.saveJournal()
                        }
                    }
                )
            }
        }

        editorController.current.value = storeController.newEntry()
    }
}

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java, *args)
}
