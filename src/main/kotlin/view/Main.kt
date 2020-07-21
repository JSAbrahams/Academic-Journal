package main.kotlin.view

import tornadofx.View
import tornadofx.borderpane

class Main : View() {
    override val root = borderpane {  }

    val history: History by inject()
    val editor: Editor by inject()
    val menu: Menu by inject()

    init {
        root.left = history.root
        root.center = editor.root
        root.top = menu.root
    }
}
