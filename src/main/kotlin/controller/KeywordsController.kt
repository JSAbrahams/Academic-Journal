package main.kotlin.controller

import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.Keyword
import tornadofx.Controller

class KeywordsController : Controller() {
    val selectedKeywordProperty = SimpleObjectProperty<Keyword>()
}
