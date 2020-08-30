package main.kotlin.model.reference

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.select
import java.time.LocalDate

data class Reference(
    val id: Int = -1,
    val itemType: String = "",
    val title: String = "",
    val authors: List<Author> = listOf(),
    val abstract: String = "",

    val publication: Int? = null,
    val volume: Int? = null,
    val issue: Int? = null,
    val pages: Pair<Int?, Int?> = Pair(null, null),

    val date: LocalDate? = null,
    val series: String = "",
    val seriesTitle: String = "",
    val seriesText: String = "",

    val journalAbbr: String = "",
    val language: String = "",
) {
    val itemTypeProperty = SimpleStringProperty(itemType)
    val titleProperty = SimpleStringProperty(title)
    val authorProperty = SimpleListProperty(authors.asObservable())
    val abstractProperty = SimpleStringProperty(abstract)
}

class ReferenceModel(property: ObjectProperty<Reference>) : ItemViewModel<Reference>(itemProperty = property) {
    val itemType = bind(autocommit = true) { property.select { it.itemTypeProperty } }
    val title = bind(autocommit = true) { property.select { it.titleProperty } }
    val authors: ObservableList<Author> = bind(autocommit = true) { property.select { it.authorProperty } }
    val abstract = bind(autocommit = true) { property.select { it.abstractProperty } }
}

