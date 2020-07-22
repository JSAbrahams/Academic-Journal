package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.asObservable

class Note(start: Int, end: Int, note: String?, reference: List<Reference>) {
    val startProperty = SimpleIntegerProperty(start)
    val endProperty = SimpleIntegerProperty(end)

    val noteProperty = SimpleStringProperty(note)
    val referencesProperty = SimpleListProperty<Reference>(reference.asObservable())
}

class NoteModel(property: ObjectProperty<Note>) : ItemViewModel<Note>(itemProperty = property) {
    val start = bind(autocommit = true) { property.get().startProperty }
    val end = bind(autocommit = true) { property.get().endProperty }
    val note = bind(autocommit = true) { property.get().noteProperty }
    val references = bind(autocommit = true) { property.get().referencesProperty }
}
