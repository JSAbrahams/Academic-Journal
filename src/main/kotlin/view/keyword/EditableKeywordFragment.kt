package main.kotlin.view.keyword

import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.model.KeywordModel
import main.kotlin.model.Tag
import tornadofx.*

class EditableKeywordFragment : ListCellFragment<Tag>() {
    val entry = KeywordModel(itemProperty)

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
