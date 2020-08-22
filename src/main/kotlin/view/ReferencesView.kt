package main.kotlin.view

import main.kotlin.Styles
import tornadofx.*

class ReferencesView : View() {
    override val root = vbox {
        addClass(Styles.container)

        text("References") { setId(Styles.title) }
        listview<String>()
    }
}
