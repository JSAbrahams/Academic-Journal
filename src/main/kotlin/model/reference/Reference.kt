package main.kotlin.model.reference

import javafx.beans.property.*
import javafx.collections.ObservableList
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.select
import java.time.LocalDate

class Reference(
    val id: Int = -1,
    itemType: String = "",
    title: String = "",
    authors: List<Author> = listOf(),
    abstract: String = "",

    publication: String = "",
    volume: String = "",
    issue: String = "",
    pages: Pair<Int, Int> = Pair(-1, -1),

    date: LocalDate = LocalDate.MIN,
    series: String = "",
    seriesTitle: String = "",
    seriesText: String = "",
    journalAbbr: String = "",
    language: String = "",

    collection: Collection?
) {
    val itemTypeProperty = SimpleStringProperty(itemType)
    val titleProperty = SimpleStringProperty(title)
    val authorProperty = SimpleListProperty(authors.asObservable())
    val abstractProperty = SimpleStringProperty(abstract)

    val publicationProperty = SimpleStringProperty(publication)
    val volumeProperty = SimpleStringProperty(volume)
    val issueProperty = SimpleStringProperty(issue)

    val pagesStartProperty = SimpleIntegerProperty(pages.first)
    val pagesEndProperty = SimpleIntegerProperty(pages.second)

    val dateProperty = SimpleObjectProperty(date)
    val seriesProperty = SimpleStringProperty(series)
    val seriesTitleProperty = SimpleStringProperty(seriesTitle)
    val seriesTextProperty = SimpleStringProperty(seriesText)
    val journalAbbrProperty = SimpleStringProperty(journalAbbr)

    val languageProperty = SimpleStringProperty(language)

    val collectionProperty = SimpleObjectProperty<Collection>(collection)
}

class ReferenceModel(property: ObjectProperty<Reference>) : ItemViewModel<Reference>(itemProperty = property) {
    val itemType = bind(autocommit = true) { property.select { it.itemTypeProperty } }
    val title = bind(autocommit = true) { property.select { it.titleProperty } }
    val authors: ObservableList<Author> = bind(autocommit = true) { property.select { it.authorProperty } }
    val abstract = bind(autocommit = true) { property.select { it.abstractProperty } }

    val publication = bind(autocommit = true) { property.select { it.publicationProperty } }
    val volume = bind(autocommit = true) { property.select { it.volumeProperty } }
    val issue = bind(autocommit = true) { property.select { it.issueProperty } }

    val pagesStart = bind(autocommit = true) { property.select { it.pagesStartProperty } }
    val pagesEnd = bind(autocommit = true) { property.select { it.pagesEndProperty } }

    val date = bind(autocommit = true) { property.select { it.dateProperty } }
    val series = bind(autocommit = true) { property.select { it.seriesProperty } }
    val seriesTitle = bind(autocommit = true) { property.select { it.seriesTitleProperty } }
    val seriesText = bind(autocommit = true) { property.select { it.seriesTextProperty } }
    val journalAbbr = bind(autocommit = true) { property.select { it.journalAbbrProperty } }

    val language = bind(autocommit = true) { property.select { it.languageProperty } }
    val collection: ObjectProperty<Collection> = bind(autocommit = false) { property.select { it.collectionProperty } }
}

