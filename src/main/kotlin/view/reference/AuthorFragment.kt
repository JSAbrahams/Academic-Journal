package main.kotlin.view.reference

import main.kotlin.Styles
import main.kotlin.model.reference.Author
import main.kotlin.model.reference.AuthorModel
import tornadofx.*

class AuthorFragment : ListCellFragment<Author>() {
    companion object {
        // According to Javafx documentation of cell height
        const val height = 24
    }

    val entry = AuthorModel(itemProperty)

    override val root = hbox {
        checkbox {
            entry.isSelected.bindBidirectional(this.selectedProperty())
        }

        text(entry.lastName)
        text(", ")
        text(entry.firstName)
    }
}

class SimpleAuthorFragment : ListCellFragment<Author>() {
    companion object {
        // According to Javafx documentation of cell height
        const val height = 24
    }

    val entry = AuthorModel(itemProperty)

    override val root = hbox {
        addClass(Styles.textEntry)
        enableWhen(entry.isSelected)

        text(entry.lastName)
        text(", ")
        text(entry.firstName)
    }
}
