package main.kotlin.view.reference

import main.kotlin.model.reference.SubCollection
import main.kotlin.model.reference.SubCollectionModel
import tornadofx.ListCellFragment
import tornadofx.checkbox
import tornadofx.hbox
import tornadofx.text

class SubCollectionFragment : ListCellFragment<SubCollection>() {
    val entry = SubCollectionModel(itemProperty)

    override val root = hbox {
        checkbox {
            entry.isSelected.bindBidirectional(this.selectedProperty())
        }
        text(entry.name)
    }
}
