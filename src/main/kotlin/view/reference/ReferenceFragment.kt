package main.kotlin.view.reference

import main.kotlin.model.reference.Reference
import main.kotlin.model.reference.ReferenceModel
import tornadofx.ListCellFragment
import tornadofx.hbox

class ReferenceFragment : ListCellFragment<Reference>() {
    val entry = ReferenceModel(itemProperty)

    override val root = hbox {}
}
