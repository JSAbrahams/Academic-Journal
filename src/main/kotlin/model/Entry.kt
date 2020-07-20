package main.kotlin.model

import main.kotlin.model.reference.Reference
import java.util.*

abstract class Notable(val start: Int, val end: Int, val note: String?)

data class Entry(
    val date: Date,
    val text: String,
    val highlight: List<Highlight>,
    val references: List<Reference>
) {
    /**
     * Get all notes in order of start position.
     */
    fun all_notes(): List<Notable> {
        val mutableList = this.highlight.toMutableList<Notable>()
        mutableList.addAll(this.references)
        return mutableList.sortedBy { notable -> notable.start }
    }
}
