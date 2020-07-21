import javafx.application.Application
import main.kotlin.view.Main
import tornadofx.App

class JournalApp : App(Main::class)

fun main(args: Array<String>) {
    Application.launch(JournalApp::class.java, *args)
}
