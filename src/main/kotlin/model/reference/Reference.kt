package main.kotlin.model.reference

import java.time.LocalDate

data class Reference(
    val itemType: String = "",
    val title: String = "",
    val author: List<Author> = listOf(),
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
)
