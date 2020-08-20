package model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.Author
import main.kotlin.model.Reference

class ReferenceTest : FreeSpec({
    "a reference" - {
        "can be retrieved from json" {
            val string = this.javaClass.getResource("/json/reference/simple.json").readText()
            val reference = Json.decodeFromString<Reference>(string)

            reference.titleProperty.get() shouldBe "title"
            reference.authorsProperty.toList() shouldBe listOf(Author(listOf("first", "last")))
        }
        "can be written to json" {
            val reference = Reference("title", listOf(Author(listOf("first", "last"))))
            val refString = Json.encodeToString(reference)
            val newRef = Json.decodeFromString<Reference>(refString)

            reference shouldBe newRef
        }
    }
})
