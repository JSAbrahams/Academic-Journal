package model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.Keyword

class KeywordTest : FreeSpec({
    "a keyword" - {
        "can be retrieved from a json" {
            val string = this.javaClass.getResource("/json/keyword.json").readText()
            val keyword = Json.decodeFromString<Keyword>(string)

            keyword.textProperty.get() shouldBe "keyword"
        }
        "can be written to json" {
            val keyword = Keyword("keyword")
            val newKeyword = Json.decodeFromString<Keyword>(Json.encodeToString(keyword))

            newKeyword shouldBe keyword
        }
    }
})
