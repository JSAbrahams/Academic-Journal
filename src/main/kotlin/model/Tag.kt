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
import java.util.*

@Serializable(with = KeywordSerializer::class)
class Tag(val id: UUID = UUID.randomUUID(), text: String = "", description: String = "", color: Color = Color.RED) {
    val textProperty = SimpleStringProperty(text)
    val descriptionProperty = SimpleStringProperty(description)
    val colorProperty = SimpleObjectProperty(color)
    val editedProperty: BooleanProperty = SimpleBooleanProperty(false)

    init {
        textProperty.onChange { editedProperty.set(true) }
    }

    override fun toString(): String = "#${textProperty.get()}"
    override fun equals(other: Any?): Boolean = other is Tag && textProperty.get() == other.textProperty.get()
    override fun hashCode(): Int = textProperty.get().hashCode()
}

/**
 * Custom KeywordSerializer to handle the ObjectProperty's.
 */
object KeywordSerializer : KSerializer<Tag> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Keyword") {
        element<String>("id")
        element<String>("text")
        element<String>("description")
        element<String>("color")
    }

    override fun serialize(encoder: Encoder, value: Tag) = encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.id.toString())
        encodeStringElement(descriptor, 1, value.textProperty.get())
        encodeStringElement(descriptor, 2, value.descriptionProperty.get())
        encodeStringElement(descriptor, 3, value.colorProperty.get().web)
    }

    override fun deserialize(decoder: Decoder): Tag = decoder.decodeStructure(descriptor) {
        var uuid = UUID.randomUUID()
        var (word, description) = Pair("", "")
        var color = Color.WHITE

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> uuid = UUID.fromString(decodeStringElement(descriptor, 0))
                1 -> word = decodeStringElement(descriptor, 1)
                2 -> description = decodeStringElement(descriptor, 2)
                3 -> color = Color.web(decodeStringElement(descriptor, 3))
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }

        Tag(uuid, word, description, color)
    }
}

class TagModel(property: ObjectProperty<Tag>) : ItemViewModel<Tag>(itemProperty = property) {
    val text = bind(autocommit = true) { property.select { it.textProperty } }
    val description = bind(autocommit = true) { property.select { it.descriptionProperty } }
    val color = bind(autocommit = true) { property.select { it.colorProperty.asString() } }
    val colorValue: Property<Color> = bind(autocommit = true) { property.select { it.colorProperty } }
    val edited = bind(autocommit = true) { property.select { it.editedProperty } }
}
