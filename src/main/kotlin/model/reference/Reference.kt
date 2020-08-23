package main.kotlin.model.reference

import java.time.LocalDate

data class Reference(
    val itemType: String,
    val title: String,
    val author: List<String>,
    val abstract: String,

    val publication: Int,
    val volume: Int,
    val issue: Int,
    val pages: Pair<Int, Int>,

    val date: LocalDate,
    val series: String,
    val seriesTitle: String,
    val seriesText: String,

    val journalAbbr: String,
    val language: String,
)
