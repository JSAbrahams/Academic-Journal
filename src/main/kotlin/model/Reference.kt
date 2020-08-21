package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import tornadofx.ItemViewModel
import tornadofx.select

@Serializable(with = ReferenceSerializer::class)
class Reference(start: Int, end: Int, val reference: Int) {
    val startProperty = SimpleIntegerProperty(start)
    val endProperty = SimpleIntegerProperty(end)

    /**
     * Load reference based on mapping from identifier to actual reference
     */
    fun loadReference(): Nothing = TODO()

    override fun equals(other: Any?): Boolean = other is Reference && startProperty.get() == other.startProperty.get()
            && endProperty.get() == other.endProperty.get() && reference == other.reference

    override fun hashCode(): Int {
        var result = startProperty.hashCode()
        result = 31 * result + endProperty.hashCode()
        return 31 * result + reference
    }
}

/**
 * Custom NoteSerializer to handle the ObjectProperty's.
 */
object ReferenceSerializer : KSerializer<Reference> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Note") {
        element<Int>("start")
        element<Int>("end")
        element<List<Long>>("reference")
    }

    override fun serialize(encoder: Encoder, value: Reference) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.startProperty.get())
        encodeIntElement(descriptor, 1, value.endProperty.get())
        encodeIntElement(descriptor, 2, value.reference)
    }

    override fun deserialize(decoder: Decoder): Reference = decoder.decodeStructure(descriptor) {
        var (start, end) = Pair(0, 0)
        var reference = -1

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> start = decodeIntElement(descriptor, 0)
                1 -> end = decodeIntElement(descriptor, 1)
                2 -> reference = decodeIntElement(descriptor, 2)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }

        Reference(start, end, reference)
    }
}

class NoteModel(property: ObjectProperty<Reference>) : ItemViewModel<Reference>(itemProperty = property) {
    val start = bind(autocommit = true) { property.select { it.startProperty } }
    val end = bind(autocommit = true) { property.select { it.endProperty } }
}
