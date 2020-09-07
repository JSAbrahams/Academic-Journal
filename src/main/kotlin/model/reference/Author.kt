package main.kotlin.model.reference

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.select

class Author(val id: Int, firstName: String = "", lastName: String = "") {
    val firstNameProperty = SimpleStringProperty(firstName)
    val lastNameProperty = SimpleStringProperty(lastName)

    val selectedProperty = SimpleBooleanProperty(true)
}

class AuthorModel(property: ObjectProperty<Author>) : ItemViewModel<Author>(itemProperty = property) {
    val firstName = bind(autocommit = true) { property.select { it.firstNameProperty } }
    val lastName = bind(autocommit = true) { property.select { it.lastNameProperty } }
    val isSelected = bind(autocommit = true) { property.select { it.selectedProperty } }
}
