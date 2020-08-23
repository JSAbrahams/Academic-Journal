package main.kotlin.view.fragment

import main.kotlin.model.reference.Author
import main.kotlin.model.reference.AuthorModel
import tornadofx.ListCellFragment
import tornadofx.text
import tornadofx.vbox

class AuthorFragment : ListCellFragment<Author>() {
    val entry = AuthorModel(itemProperty)

    override val root = vbox {
        text(entry.surname)
    }
}
