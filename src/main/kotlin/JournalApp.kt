import javafx.application.Application
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.*

class Main: View() {
    override val root: BorderPane by fxml("fxml/main.fxml")
}

class JournalApp : App() {
    override val primaryView = Main::class

    override fun start(stage: Stage) {
        importStylesheet("resources/style.css")
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java,*args)
}
