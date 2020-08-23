package main.kotlin.view.fragment

import main.kotlin.model.Keyword
import main.kotlin.model.KeywordModel
import tornadofx.ListCellFragment
import tornadofx.hbox
import tornadofx.text

class SimpleKeywordFragment : ListCellFragment<Keyword>() {
    val entry = KeywordModel(itemProperty)

    override val root = hbox {
        text(entry.value)
    }
}
