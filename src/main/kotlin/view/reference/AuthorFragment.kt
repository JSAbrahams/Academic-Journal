package main.kotlin.view.reference

import main.kotlin.model.reference.Author
import main.kotlin.model.reference.AuthorModel
import tornadofx.ListCellFragment
import tornadofx.hbox
import tornadofx.text

class AuthorFragment : ListCellFragment<Author>() {
    val entry = AuthorModel(itemProperty)

    override val root = hbox {
        text(entry.lastName)
        text(", ")
        text(entry.firstName)
    }
}
