package main.kotlin.view.keyword

import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.model.Keyword
import main.kotlin.model.KeywordModel
import tornadofx.*

class EditableKeywordFragment : ListCellFragment<Keyword>() {
    val entry = KeywordModel(itemProperty)

    override val root = vbox {
        addClass(Styles.nestedContainer)
        hbox {
            addClass(Styles.nestedContainer)

            textfield(entry.text) {
                hgrow = Priority.ALWAYS
                background = Background(BackgroundFill(entry.colorValue.value, Styles.keywordRadii, Insets.EMPTY))
                entry.colorValue.onChange {
                    if (it != null) background = Background(BackgroundFill(it, Styles.keywordRadii, Insets.EMPTY))
                }
            }
            colorpicker(entry.colorValue.value) {
                setOnAction { entry.colorValue.setValue(value) }
            }
        }

        textfield(entry.description)
    }
}
