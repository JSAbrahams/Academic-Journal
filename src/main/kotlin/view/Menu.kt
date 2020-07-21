package main.kotlin.view

import tornadofx.*

class Menu : View() {
    override val root = menubar {
        menu("File") {
            item("Open")
            radiomenuitem("Open Recent")
            separator()
            item("Save")
            item("Save As")
            separator()
            item("Close")
        }
        menu("Help") {
            item("About")
        }
    }
}