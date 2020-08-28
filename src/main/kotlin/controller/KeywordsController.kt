package main.kotlin.controller

import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.Keyword
import tornadofx.Controller
import tornadofx.select

class KeywordsController : Controller() {
    val editorController: EditorController by inject()

    val selectedKeywordProperty = SimpleObjectProperty<Keyword>()
    val allKeywords = editorController.current.select { it.keywordsProperty }
}
