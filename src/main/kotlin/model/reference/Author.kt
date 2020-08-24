package main.kotlin.model.reference

import javafx.beans.property.SimpleStringProperty

class Author(firstName: String = "", lastName: String = "") {
    val firstName = SimpleStringProperty(firstName)
    val lastName = SimpleStringProperty(lastName)
}
