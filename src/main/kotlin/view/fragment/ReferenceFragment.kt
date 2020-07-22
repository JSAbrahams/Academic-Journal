package main.kotlin.view.fragment

import main.kotlin.model.Reference
import main.kotlin.model.ReferenceModel
import tornadofx.*

class ReferenceFragment : ListCellFragment<Reference>() {
    val entry = ReferenceModel(itemProperty)

    override val root = vbox {
        hbox {
            text("Title: ")
            text(entry.title)
        }
        hbox {
            text("Authors: ")
            listview(entry.authors) {
                cellFragment(AuthorFragment::class)
            }
        }
    }
}
