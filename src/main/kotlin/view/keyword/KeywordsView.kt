package main.kotlin.view.keyword

import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.JournalController
import main.kotlin.controller.KeywordsController
import main.kotlin.model.Keyword
import tornadofx.*

class KeywordsView : View() {
    val keywordsController: KeywordsController by inject()
    val journalController: JournalController by inject()
    val editorController: EditorController by inject()

    override val root = vbox {
        addClass(Styles.customContainer)

        text("Tags") {
            addClass(Styles.title)
        }

        listview(journalController.journal.select { it.keywordList }) {
            cellFragment(EditableKeywordFragment::class)

            keywordsController.selectedKeywordProperty.onChange {
                selectionModel.select(it)
                focusModel.focus(selectionModel.selectedIndex)
            }
        }

        button("+") {
            disableWhen(editorController.isEditMode.not())
            action {
                if (journalController.journal.isNotNull.get()) journalController.journal.value.addKeyword(Keyword())
            }
        }
    }
}
