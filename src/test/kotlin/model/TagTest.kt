package model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.paint.Color
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.Tag
import java.util.*

class TagTest : FreeSpec({
    "a tag" - {
        "can be retrieved from a json" {
            val string = this.javaClass.getResource("/json/tags.json").readText()
            val tag = Json.decodeFromString<Tag>(string)

            tag.textProperty.get() shouldBe "keyword"
        }
        "can be written to json" {
            val tag = Tag(id = UUID.randomUUID(), text = "240O", description = "d52", color = Color.GREEN)
            val newTag = Json.decodeFromString<Tag>(Json.encodeToString(tag))

            newTag shouldBe tag
        }
    }
})
