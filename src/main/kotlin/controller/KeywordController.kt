package main.kotlin.controller

import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.journal.Tag
import tornadofx.Controller

class KeywordController : Controller() {
    val selectedKeywordProperty = SimpleObjectProperty<Tag>()
}
