package main.kotlin.view

import tornadofx.View
import tornadofx.borderpane

class Main : View() {
    override val root = borderpane {
        top(Menu::class)
        left(Entries::class)
        center(Editor::class)
        right(References::class)
    }
}
