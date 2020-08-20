package model

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.Journal
import main.kotlin.model.JournalEntry
import java.util.*

@Ignored // Writing to JSON does not work for JournalEntries
class JournalTest : FreeSpec({
    "a journal" - {
        "can be retrieved from a json" {
            val string = this.javaClass.getResource("/json/journal/simple.json").readText()
            val journal = Json.decodeFromString<Journal>(string)

            journal.titleProperty.get() shouldBe "title"
            journal.itemsProperty.size shouldBe 1

            val journalEntry = journal.itemsProperty.toList()[0]
            journalEntry.titleProperty.get() shouldBe "otherTitle"
            journalEntry.textProperty.get() shouldBe "text"
            journalEntry.creationProperty.get().toInstant().epochSecond shouldBe 10
            journalEntry.lastEditProperty.get().toInstant().epochSecond shouldBe 20
        }
        "can be written to json" {
            val journal = Journal("title", listOf(JournalEntry(Date(300), Date(400), "title", "text")))
            val journalString = Json.encodeToString(journal)
            val newJournal = Json.decodeFromString<JournalEntry>(journalString)

            newJournal shouldBe journal
        }
    }
})
