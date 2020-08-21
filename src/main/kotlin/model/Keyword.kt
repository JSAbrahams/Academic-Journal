package main.kotlin.model

import javafx.beans.property.*
import javafx.scene.paint.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import tornadofx.ItemViewModel
import tornadofx.onChange
import tornadofx.select

@Serializable(with = KeywordSerializer::class)
class Keyword(text: String = "") {
    val textProperty = SimpleStringProperty(text)
    val colorProperty = SimpleObjectProperty<Color>(Color.WHITE)
    val editedProperty: BooleanProperty = SimpleBooleanProperty(false)

    init {
        textProperty.onChange { editedProperty.set(true) }
    }

    override fun toString(): String = textProperty.get()
    override fun equals(other: Any?): Boolean = other is Keyword && textProperty.get() == other.textProperty.get()
    override fun hashCode(): Int = textProperty.get().hashCode()
}

/**
 * Custom KeywordSerializer to handle the ObjectProperty's.
 */
object KeywordSerializer : KSerializer<Keyword> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Keyword", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Keyword) = encoder.encodeString(value.textProperty.get())
    override fun deserialize(decoder: Decoder): Keyword = Keyword(decoder.decodeString())
}

class KeywordModel(property: ObjectProperty<Keyword>) : ItemViewModel<Keyword>(itemProperty = property) {
    val value = bind(autocommit = true) { property.select { it.textProperty } }
    val color = bind(autocommit = true) { property.select { it.colorProperty } }
    val edited = bind(autocommit = true) { property.select { it.editedProperty } }
}
