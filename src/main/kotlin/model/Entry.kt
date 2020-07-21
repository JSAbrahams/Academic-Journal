package main.kotlin.model

import java.util.Date

data class Entry(
    val date: Date,
    val text: String,
    val references: List<Reference>,
    val keywords: List<String>
)
