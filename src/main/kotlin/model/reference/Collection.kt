package main.kotlin.model.reference

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.select

class Collection(val id: Int, name: String) {
    val nameProperty = SimpleStringProperty(name)
    val selectedProperty = SimpleBooleanProperty(true)
}

class CollectionModel(property: ObjectProperty<Collection>) : ItemViewModel<Collection>(itemProperty = property) {
    val name = bind(autocommit = true) { property.select { it.nameProperty } }
    val isSelected = bind(autocommit = true) { property.select { it.selectedProperty } }
}
