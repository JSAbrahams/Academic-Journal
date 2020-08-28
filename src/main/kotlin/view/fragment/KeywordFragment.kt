package main.kotlin.view.fragment

import javafx.scene.layout.Priority
import main.kotlin.controller.EditorController
import main.kotlin.model.Keyword
import main.kotlin.model.KeywordModel
import tornadofx.*

class KeywordFragment : ListCellFragment<Keyword>() {
    private val editorController: EditorController by inject()

    val entry = KeywordModel(itemProperty)

    override val root = hbox {
        region {
            vgrow = Priority.ALWAYS
            val height = heightProperty()
            circle(radius = 3) {
                centerYProperty().bind(height.divide(2.0))
                visibleWhen(entry.edited)
                managedWhen(entry.edited)
            }
        }

        textfield(entry.value) {
            disableWhen(editorController.isEditMode.not())
        }
    }
}
