package main.kotlin.model

import javafx.beans.property.*
import javafx.scene.paint.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import main.kotlin.web
import tornadofx.ItemViewModel
import tornadofx.onChange
import tornadofx.select

@Serializable(with = KeywordSerializer::class)
class Keyword(text: String = "", description: String = "", color: Color = Color.RED) {
    val textProperty = SimpleStringProperty(text)
    val descriptionProperty = SimpleStringProperty(description)
    val colorProperty = SimpleObjectProperty(color)
    val editedProperty: BooleanProperty = SimpleBooleanProperty(false)

    init {
        textProperty.onChange { editedProperty.set(true) }
    }

    override fun toString(): String = "#${textProperty.get()}"
    override fun equals(other: Any?): Boolean = other is Keyword && textProperty.get() == other.textProperty.get()
    override fun hashCode(): Int = textProperty.get().hashCode()
}

/**
 * Custom KeywordSerializer to handle the ObjectProperty's.
 */
object KeywordSerializer : KSerializer<Keyword> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Keyword") {
        element<String>("word")
        element<String>("description")
        element<String>("color")
    }

    override fun serialize(encoder: Encoder, value: Keyword) = encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.textProperty.get())
        encodeStringElement(descriptor, 1, value.descriptionProperty.get())
        encodeStringElement(descriptor, 2, value.colorProperty.get().web)
    }

    override fun deserialize(decoder: Decoder): Keyword = decoder.decodeStructure(descriptor) {
        var (word, description) = Pair("", "")
        var color = Color.WHITE

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> word = decodeStringElement(descriptor, 0)
                1 -> description = decodeStringElement(descriptor, 1)
                2 -> color = Color.web(decodeStringElement(descriptor, 2))
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }

        Keyword(word, description, color)
    }
}

class KeywordModel(property: ObjectProperty<Keyword>) : ItemViewModel<Keyword>(itemProperty = property) {
    val text = bind(autocommit = true) { property.select { it.textProperty } }
    val description = bind(autocommit = true) { property.select { it.descriptionProperty } }
    val color = bind(autocommit = true) { property.select { it.colorProperty.asString() } }
    val colorValue = bind(autocommit = true) { property.select { it.colorProperty } }
    val edited = bind(autocommit = true) { property.select { it.editedProperty } }
}
