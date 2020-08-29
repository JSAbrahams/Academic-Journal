package main.kotlin.controller

import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.Keyword
import tornadofx.Controller
import tornadofx.select

class KeywordsController : Controller() {
    val journalCollection: JournalController by inject()

    val selectedKeywordProperty = SimpleObjectProperty<Keyword>()

    val allKeywords = journalCollection.journal.select { it.keywords }
}
