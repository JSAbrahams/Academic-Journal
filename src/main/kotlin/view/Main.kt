package main.kotlin.view

import tornadofx.View
import tornadofx.borderpane

class Main : View() {
    override val root = borderpane {
        top(Menu::class)
        center(Editor::class)
        left(History::class)
    }
}
