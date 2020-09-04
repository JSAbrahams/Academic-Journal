package main.kotlin.view.tag

import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.JournalController
import main.kotlin.controller.KeywordController
import main.kotlin.model.Tag
import main.kotlin.view.JournalView
import tornadofx.*

class TagView : JournalView() {
    val keywordController: KeywordController by inject()
    val journalController: JournalController by inject()
    val editorController: EditorController by inject()

    override val root = vbox {
        addClass(Styles.customContainer)

        text("Tags") {
            addClass(Styles.title)
        }

        listview(journalController.journalProperty.select { it.keywordList }) {
            cellFragment(EditableTagFragment::class)

            keywordController.selectedKeywordProperty.onChange {
                selectionModel.select(it)
                focusModel.focus(selectionModel.selectedIndex)
            }
        }

        button("+") {
            disableWhen(editorController.isEditable.not())
            action {
                if (journalController.journalProperty.isNotNull.get()) journalController.journalProperty.value.addKeyword(
                    Tag()
                )
            }
        }
    }
}
