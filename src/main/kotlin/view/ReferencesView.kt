package main.kotlin.view

import main.kotlin.Styles
import tornadofx.*

class ReferencesView : View() {
    override val root = vbox {
        addClass(Styles.customContainer)

        text("References") { setId(Styles.title) }

        listview<String>()

        hbox {
            addClass(Styles.buttons)
            button("+") {
                isVisible = false
            }
        }
    }
}
