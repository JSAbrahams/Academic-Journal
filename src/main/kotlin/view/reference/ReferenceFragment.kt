package main.kotlin.view.reference

import javafx.beans.property.SimpleBooleanProperty
import main.kotlin.model.reference.Reference
import main.kotlin.model.reference.ReferenceModel
import tornadofx.*

class ReferenceFragment : ListCellFragment<Reference>() {
    val entry = ReferenceModel(itemProperty)

    val showAbstract = SimpleBooleanProperty(false)

    override val root = hbox {
        onLeftClick {
            showAbstract.set(!showAbstract.get())
        }

        hbox {
            text(entry.itemType)
            text("•")
            text(entry.title)
        }
        text(entry.abstract).visibleWhen(showAbstract)

        button("Open").action { }
    }
}
