package model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import main.kotlin.model.Author

class AuthorTest : FreeSpec({
    "an author" - {
        "can be retrieved from a json" {
            val string = this.javaClass.getResource("/json/author/simple.json").readText()
            val author = Json.decodeFromString<Author>(string)

            author.firstProperty.get() shouldBe "first"
            author.surnameProperty.get() shouldBe "last"
        }
        "can be retrieved from a json even if it has one name" {
            val string = this.javaClass.getResource("/json/author/one_name.json").readText()
            val author = Json.decodeFromString<Author>(string)

            author.firstProperty.get() shouldBe "first"
            author.surnameProperty.get() shouldBe ""

        }
        "can be retrieved from a json even if it has more than two names" {
            val string = this.javaClass.getResource("/json/author/three_names.json").readText()
            val author = Json.decodeFromString<Author>(string)

            author.firstProperty.get() shouldBe "first"
            author.surnameProperty.get() shouldBe "last"
            author.namesProperty.get().toList() shouldBe listOf("first", "middle", "last")
        }
    }
})
