package model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.ReferencePosition

class ReferencePositionTest : FreeSpec({
    "a reference position" - {
        "can be retrieved from a json" {
            val string = this.javaClass.getResource("/json/reference-position.json").readText()
            val reference = Json.decodeFromString<ReferencePosition>(string)

            reference.startProperty.get() shouldBe 612
            reference.endProperty.get() shouldBe 585
            reference.referenceId shouldBe 660
        }
        "can be written to json" {
            val keyword = ReferencePosition(referenceId = 860, start = 479, end = 778)
            val netReferencePosition = Json.decodeFromString<ReferencePosition>(Json.encodeToString(keyword))

            netReferencePosition shouldBe keyword
        }
    }
})
