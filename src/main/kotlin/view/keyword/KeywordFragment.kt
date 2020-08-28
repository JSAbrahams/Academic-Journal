package main.kotlin.view.keyword

import main.kotlin.controller.KeywordsController
import main.kotlin.model.Keyword
import main.kotlin.model.KeywordModel
import tornadofx.ListCellFragment
import tornadofx.hbox
import tornadofx.text

class KeywordFragment : ListCellFragment<Keyword>() {
    val entry = KeywordModel(itemProperty)

    val keywordController: KeywordsController by inject()

    val keywordsView: KeywordsView by inject()

    override val root = hbox {
        text(entry.value)

        setOnMouseClicked {
            if (it.clickCount == 2) {
                keywordController.selectedKeywordProperty.set(entry.item)
                keywordsView.openWindow(owner = currentStage, block = true)
            }
        }
    }
}
