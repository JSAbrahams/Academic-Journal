package main.kotlin.view.keyword

import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.model.Keyword
import main.kotlin.model.KeywordModel
import main.kotlin.web
import tornadofx.*

class EditableKeywordFragment : ListCellFragment<Keyword>() {
    val entry = KeywordModel(itemProperty)

    override val root = vbox {
        addClass(Styles.nestedContainer)
        hbox {
            addClass(Styles.nestedContainer)

            text("#")
            textfield(entry.text) {
                hgrow = Priority.ALWAYS
                promptText = "Name"

                styleProperty().bind(
                    Bindings.createObjectBinding(
                        { "-fx-text-inner-color: ${entry.colorValue.value?.invert()?.web}; " },
                        entry.colorValue
                    )
                )

                background = Background(BackgroundFill(entry.colorValue.value, Styles.keywordRadii, Insets.EMPTY))
                entry.colorValue.onChange {
                    background = Background(BackgroundFill(it, Styles.keywordRadii, Insets.EMPTY))
                }
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
