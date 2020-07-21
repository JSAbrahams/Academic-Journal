package main.kotlin.view

import main.kotlin.controller.HistoryController
import tornadofx.View
import tornadofx.button
import tornadofx.hbox
import tornadofx.scrollpane
import tornadofx.vbox

class History : View() {
    val historyController: HistoryController by inject()

    val cards = vbox { }

    override val root = vbox {
        scrollpane { cards }
        hbox {
            button("+") { }
        }
    }
}
