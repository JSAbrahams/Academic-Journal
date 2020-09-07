package main.kotlin.model.reference

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.select

class SubCollection {
    val nameProperty = SimpleStringProperty()
    val selectedProperty = SimpleBooleanProperty(true)
}

class SubCollectionModel(property: ObjectProperty<SubCollection>) :
    ItemViewModel<SubCollection>(itemProperty = property) {
    val name = bind(autocommit = true) { property.select { it.nameProperty } }
    val isSelected = bind(autocommit = true) { property.select { it.selectedProperty } }
}
