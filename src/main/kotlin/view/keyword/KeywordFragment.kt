package main.kotlin.view.keyword

import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import main.kotlin.Styles
import main.kotlin.controller.KeywordsController
import main.kotlin.model.Keyword
import main.kotlin.model.KeywordModel
import tornadofx.ListCellFragment
import tornadofx.hbox
import tornadofx.onChange
import tornadofx.text

class KeywordFragment : ListCellFragment<Keyword>() {
    val entry = KeywordModel(itemProperty)

    val keywordController: KeywordsController by inject()

    val keywordsView: KeywordsView by inject()

    override val root = hbox {
        text(entry.text) {
            fillProperty().bind(Bindings.createObjectBinding({ entry.colorValue.value.invert() }, entry.colorValue))

            background = Background(BackgroundFill(entry.colorValue.value, Styles.keywordRadii, Insets.EMPTY))
            entry.colorValue.onChange {
                background = Background(BackgroundFill(it, Styles.keywordRadii, Insets.EMPTY))
            }
        }

        setOnMouseClicked {
            if (it.clickCount == 2) {
                keywordController.selectedKeywordProperty.set(entry.item)
                keywordsView.openWindow(owner = currentStage, block = true)
            }
        }
    }
}
