package main.kotlin.view.tag

import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.model.journal.Tag
import main.kotlin.model.journal.TagModel
import tornadofx.*

class EditableTagFragment : ListCellFragment<Tag>() {
    val entry = TagModel(itemProperty)

    override val root = vbox {
        addClass(Styles.nestedContainer)
        hbox {
            addClass(Styles.nestedContainer)

            text("#")
            textfield(entry.text) {
                hgrow = Priority.ALWAYS
                promptText = "Name"
            }
            colorpicker(entry.colorValue.value) {
                setOnAction { entry.colorValue.setValue(value) }
            }
        }

        textfield(entry.description) {
            promptText = "Description"
        }
    }
}
