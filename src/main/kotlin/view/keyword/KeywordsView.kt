package main.kotlin.view.keyword

import main.kotlin.controller.KeywordsController
import tornadofx.*

class KeywordsView : View() {
    val keywordsController: KeywordsController by inject()

    override val root = gridpane {
        row {
            listview(keywordsController.allKeywords) {
                cellFragment(EditableKeywordFragment::class)

                keywordsController.selectedKeywordProperty.onChange {
                    selectionModel.select(it)
                    focusModel.focus(selectionModel.selectedIndex)
                }
            }
        }
    }
}
