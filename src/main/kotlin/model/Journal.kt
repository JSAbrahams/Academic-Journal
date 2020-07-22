package main.kotlin.model

import javafx.beans.property.SimpleListProperty
import java.io.File

class Journal {
    val items = SimpleListProperty<JournalEntry>()

    companion object {
        fun load(file: File): Journal = TODO()
    }

    fun save(file: File): Nothing = TODO()
}
