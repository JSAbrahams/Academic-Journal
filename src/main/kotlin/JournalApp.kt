package main.kotlin

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.*
import javafx.stage.Stage
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.MainView
import tornadofx.App
import tornadofx.select
import tornadofx.selectBoolean
import tornadofx.warning

class JournalApp : App(MainView::class) {
    val storeController: StoreController by inject()
    val editorController: EditorController by inject()

    override fun start(stage: Stage) {
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
        }

        editorController.current.value = storeController.newEntry()
    }
}

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java, *args)
}
