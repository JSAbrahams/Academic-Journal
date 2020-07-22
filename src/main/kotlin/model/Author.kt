package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.asObservable

class Author(first: String, surname: String, names: List<String>) {
    val firstProperty = SimpleStringProperty(first)
    val surnameProperty = SimpleStringProperty(surname)
    val namesProperty = SimpleListProperty(names.asObservable())
}

class AuthorModel(property: ObjectProperty<Author>) : ItemViewModel<Author>(itemProperty = property) {
    val start = bind(autocommit = true) { property.get().firstProperty }
    val surname = bind(autocommit = true) { property.get().surnameProperty }
    val names = bind(autocommit = true) { property.get().namesProperty }
}
