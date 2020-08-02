package main.kotlin

import javafx.application.Application
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.Main
import tornadofx.App
import tornadofx.selectBoolean
import tornadofx.warning

class JournalApp : App(Main::class) {
    val storeController: StoreController by inject()
    val editorController: EditorController by inject()

    override fun start(primaryStage: Stage) {
        super.start(primaryStage)

        primaryStage.setOnCloseRequest {
            if (storeController.journal.isNotNull.get() && storeController.journal.selectBoolean { it.editedProperty }.value) {
                warning(
                    "Journal still open, do you wish to save first?",
                    buttons = *arrayOf<ButtonType>(
                        ButtonType.OK,
                        ButtonType.CANCEL,
                        ButtonType.CLOSE
                    ),
                    owner = primaryStage.owner,
                    actionFn = { buttonType ->
                        when (buttonType) {
                            ButtonType.CANCEL -> it.consume()
                            ButtonType.OK -> storeController.saveJournal()
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
