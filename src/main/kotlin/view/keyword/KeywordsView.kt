package main.kotlin.view.keyword

import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.KeywordsController
import main.kotlin.model.Keyword
import tornadofx.*

class KeywordsView : View() {
    val keywordsController: KeywordsController by inject()
    val editorController: EditorController by inject()

    override val root = vbox {
        addClass(Styles.customContainer)

        text("Keywords") {
            addClass(Styles.title)
        }

        listview(keywordsController.allKeywords) {
            cellFragment(EditableKeywordFragment::class)

            keywordsController.selectedKeywordProperty.onChange {
                selectionModel.select(it)
                focusModel.focus(selectionModel.selectedIndex)
            }
        }

        button("+") {
            disableWhen(editorController.isEditMode.not())
            action {
                if (keywordsController.allKeywords.isBound) keywordsController.allKeywords.value.add(Keyword())
            }
        }
    }
}
