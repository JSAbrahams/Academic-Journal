package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.select

abstract class Reference(title: String, authors: List<Author>) {
    val titleProperty = SimpleStringProperty(title)
    val authorsProperty = SimpleListProperty(authors.asObservable())
}

class ReferenceModel(property: ObjectProperty<Reference>) : ItemViewModel<Reference>(itemProperty = property) {
    val title = bind(autocommit = true) { property.select { it.titleProperty } }
    val authors = bind(autocommit = true) { property.select { it.authorsProperty } }
}

class BibtexReference(title: String, authors: List<Author>) : Reference(title, authors)
