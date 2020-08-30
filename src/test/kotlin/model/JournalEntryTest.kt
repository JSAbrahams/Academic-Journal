package model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.JournalEntry
import java.time.LocalDateTime
import java.time.ZoneOffset

class JournalEntryTest : FreeSpec({
    "an journal entry" - {
        "can be retrieved from a json" {
            val string = this.javaClass.getResource("/json/journal_entry/simple.json").readText()
            val journalEntry = Json.decodeFromString<JournalEntry>(string)

            journalEntry.titleProperty.get() shouldBe "testTitle"
            journalEntry.textProperty.get() shouldBe "text"
            journalEntry.creationProperty.get().toEpochSecond(ZoneOffset.UTC) shouldBe 10
            journalEntry.lastEditProperty.get().toEpochSecond(ZoneOffset.UTC) shouldBe 20
        }
        "can be written to json" {
            val entry = JournalEntry(
                LocalDateTime.ofEpochSecond(100, 0, ZoneOffset.UTC),
                LocalDateTime.ofEpochSecond(200, 0, ZoneOffset.UTC),
                "title",
                "text"
            )
            val newEntry = Json.decodeFromString<JournalEntry>(Json.encodeToString(entry))

            newEntry shouldBe entry
        }
    }
})
