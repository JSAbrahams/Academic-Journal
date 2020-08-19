package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.select

class Author(names: List<String>) {
    val firstProperty = SimpleStringProperty(if (names.isNotEmpty()) names[0] else "")
    val surnameProperty = SimpleStringProperty(if (names.size > 1) names.last() else "")
    val namesProperty = SimpleListProperty(names.asObservable())
}

class AuthorModel(property: ObjectProperty<Author>) : ItemViewModel<Author>(itemProperty = property) {
    val start = bind(autocommit = true) { property.select { it.firstProperty } }
    val surname = bind(autocommit = true) { property.select { it.surnameProperty } }
    val names = bind(autocommit = true) { property.select { it.namesProperty } }
}
