package main.kotlin.view.reference

import main.kotlin.model.reference.Collection
import main.kotlin.model.reference.CollectionModel
import tornadofx.ListCellFragment
import tornadofx.checkbox
import tornadofx.hbox
import tornadofx.text

class CollectionFragment : ListCellFragment<Collection>() {
    val entry = CollectionModel(itemProperty)

    override val root = hbox {
        checkbox {
            entry.isSelected.bindBidirectional(this.selectedProperty())
        }

        text(entry.name)
    }
}
