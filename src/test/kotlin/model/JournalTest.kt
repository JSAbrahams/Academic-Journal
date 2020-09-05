package model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.journal.Journal
import main.kotlin.model.journal.JournalEntry
import main.kotlin.model.journal.Tag
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class JournalTest : FreeSpec({
    "a journal" - {
        "can be retrieved from a json" {
            val string = this.javaClass.getResource("/json/journal/simple.json").readText()
            val journal = Json.decodeFromString<Journal>(string)

            journal.titleProperty.get() shouldBe "title"
            journal.entriesProperty.size shouldBe 1
            journal.tags.size shouldBe 1
            journal.entriesProperty.size shouldBe 1
        }

        "can be written to json" {
            val journal = Journal(
                "title", listOf(
                    JournalEntry(
                        LocalDateTime.ofEpochSecond(300, 0, ZoneOffset.UTC),
                        LocalDateTime.ofEpochSecond(400, 0, ZoneOffset.UTC), "title", "text"
                    )
                )
            )
            val newJournal = Json.decodeFromString<Journal>(Json.encodeToString(journal))

            newJournal shouldBe journal
        }
        "can be written to json with keywords" {
            val journal = Journal(
                title = "title",
                tags = setOf(Tag(UUID.randomUUID())),
                items = listOf(
                    JournalEntry(
                        LocalDateTime.ofEpochSecond(300, 0, ZoneOffset.UTC),
                        LocalDateTime.ofEpochSecond(400, 0, ZoneOffset.UTC), "title", "text"
                    )
                )
            )
            val newJournal = Json.decodeFromString<Journal>(Json.encodeToString(journal))

            newJournal shouldBe journal
        }
        "can be written to json with tags" {
            val tag = Tag(UUID.randomUUID())
            val journal = Journal(
                title = "title",
                tags = setOf(tag),
                items = listOf(
                    JournalEntry(
                        creation = LocalDateTime.ofEpochSecond(300, 0, ZoneOffset.UTC),
                        lastEdit = LocalDateTime.ofEpochSecond(400, 0, ZoneOffset.UTC),
                        title = "bh9nc",
                        text = "731fsI7M",
                        tags = setOf(tag.id)
                    )
                )
            )
            val newJournal = Json.decodeFromString<Journal>(Json.encodeToString(journal))

            newJournal shouldBe journal
        }
    }
})
