package model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.Note

class NoteTest : FreeSpec({
    "a note" - {
        "can be retrieved from json" {
            val string = this.javaClass.getResource("/json/note/simple.json").readText()
            val note = Json.decodeFromString<Note>(string)

            note.startProperty.get() shouldBe 10
            note.endProperty.get() shouldBe 20
        }
        "can be written to json" {
            val note = Note(start = 15, end = 25)
            val noteString = Json.encodeToString(note)
            val newNote = Json.decodeFromString<Note>(noteString)

            note shouldBe newNote
        }

        "can be retrieved from json if text included" {

        }
        "can be written to json if text included" {

        }

        "can be retrieved from json if text and keywords included" {

        }
        "can be written to json if text and keywords included" {

        }
    }
})
