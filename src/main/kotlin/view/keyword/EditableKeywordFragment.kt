package main.kotlin.view.keyword

import main.kotlin.model.Keyword
import main.kotlin.model.KeywordModel
import tornadofx.ListCellFragment
import tornadofx.hbox
import tornadofx.text
import tornadofx.textfield

class EditableKeywordFragment : ListCellFragment<Keyword>() {
    val entry = KeywordModel(itemProperty)

    override val root = hbox {
        textfield(entry.text)
        textfield(entry.description)
        text(entry.color)
    }
}
