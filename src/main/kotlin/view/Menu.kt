package main.kotlin.view

import main.kotlin.controller.menu.FileController
import main.kotlin.controller.menu.HelpController
import tornadofx.View
import tornadofx.item
import tornadofx.menu
import tornadofx.menubar
import tornadofx.radiomenuitem
import tornadofx.separator

class Menu : View() {
    val fileController: FileController by inject()
    val helpController: HelpController by inject()

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
