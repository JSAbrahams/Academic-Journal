package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.asObservable

abstract class Reference(title: String, authors: List<Author>) {
    val titleProperty = SimpleStringProperty(title)
    val authorsProperty = SimpleListProperty(authors.asObservable())
}

class ReferenceModel(property: ObjectProperty<Reference>) : ItemViewModel<Reference>(itemProperty = property) {
    val title = bind(autocommit = true) { property.get().titleProperty }
    val authors = bind(autocommit = true) { property.get().authorsProperty }
}

class BibtexReference(title: String, authors: List<Author>) : Reference(title, authors)
