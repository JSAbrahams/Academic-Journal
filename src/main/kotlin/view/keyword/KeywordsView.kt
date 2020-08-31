package main.kotlin.view.keyword

import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.JournalController
import main.kotlin.controller.KeywordController
import main.kotlin.model.Tag
import tornadofx.*

class KeywordsView : View() {
    val keywordController: KeywordController by inject()
    val journalController: JournalController by inject()
    val editorController: EditorController by inject()

    override val root = vbox {
        addClass(Styles.customContainer)

        text("Tags") {
            addClass(Styles.title)
        }

        listview(journalController.journalProperty.select { it.keywordList }) {
            cellFragment(EditableKeywordFragment::class)

            keywordController.selectedKeywordProperty.onChange {
                selectionModel.select(it)
                focusModel.focus(selectionModel.selectedIndex)
            }
        }

        button("+") {
            disableWhen(editorController.isEditMode.not())
            action {
                if (journalController.journalProperty.isNotNull.get()) journalController.journalProperty.value.addKeyword(
                    Tag()
                )
            }
        }
    }
}
