import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.stage.Stage
import main.kotlin.controller.StoreController
import main.kotlin.view.Main
import tornadofx.App

class JournalApp : App(Main::class) {
    val storeController: StoreController by inject()

    override fun start(stage: Stage) {
        stage.titleProperty().bind(
            Bindings.`when`(storeController.location.isNull)
                .then("[Unsaved]")
                .otherwise(storeController.location.asString())
        )

        super.start(stage)
    }
}

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java, *args)
}
