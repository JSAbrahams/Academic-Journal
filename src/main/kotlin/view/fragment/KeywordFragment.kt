package main.kotlin.view.fragment

import main.kotlin.controller.EditorController
import main.kotlin.model.Keyword
import main.kotlin.model.KeywordModel
import tornadofx.*

class KeywordFragment : ListCellFragment<Keyword>() {
    private val editorController: EditorController by inject()

    val entry = KeywordModel(itemProperty)

    override val root = hbox {
        circle(radius = 3).visibleWhen(entry.edited)
        textfield(entry.value) {
            disableWhen(editorController.editMode.not())
        }
    }
}
