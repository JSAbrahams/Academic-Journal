package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import main.kotlin.model.reference.Reference
import tornadofx.ItemViewModel
import tornadofx.select

@Serializable(with = ReferencePositionSerializer::class)
class ReferencePosition(val referenceId: Int, start: Int, end: Int) {
    val startProperty = SimpleIntegerProperty(start)
    val endProperty = SimpleIntegerProperty(end)
    val referenceProperty = SimpleObjectProperty<Reference>()

    /**
     * Set reference directly.
     *
     * Useful if all references have already been loaded.
     */
    constructor(reference: Reference, start: Int, end: Int) : this(reference.id, start, end) {
        referenceProperty.set(reference)
    }

    /**
     * Load reference based on mapping from identifier to actual reference.
     */
    fun loadReference(referenceMapping: Map<Int, Reference>) = referenceProperty.set(referenceMapping[referenceId])

    override fun equals(other: Any?): Boolean = other is ReferencePosition
            && startProperty.get() == other.startProperty.get()
            && endProperty.get() == other.endProperty.get() && referenceId == other.referenceId

    override fun hashCode(): Int =
        31 * (31 * startProperty.get().hashCode() + endProperty.get().hashCode()) + referenceId
}

/**
 * Custom NoteSerializer to handle the ObjectProperty's.
 */
object ReferencePositionSerializer : KSerializer<ReferencePosition> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Note") {
        element<Int>("start")
        element<Int>("end")
        element<List<Int>>("reference")
    }

    override fun serialize(encoder: Encoder, value: ReferencePosition) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.startProperty.get())
        encodeIntElement(descriptor, 1, value.endProperty.get())
        encodeIntElement(descriptor, 2, value.referenceId)
    }

    override fun deserialize(decoder: Decoder): ReferencePosition = decoder.decodeStructure(descriptor) {
        var (start, end) = Pair(0, 0)
        var referenceId = -1

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> start = decodeIntElement(descriptor, 0)
                1 -> end = decodeIntElement(descriptor, 1)
                2 -> referenceId = decodeIntElement(descriptor, 2)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }

        ReferencePosition(referenceId, start, end)
    }
}

class NoteModel(property: ObjectProperty<ReferencePosition>) :
    ItemViewModel<ReferencePosition>(itemProperty = property) {
    val start = bind(autocommit = true) { property.select { it.startProperty.asString() } }
    val end = bind(autocommit = true) { property.select { it.endProperty.asString() } }
    val reference: ObjectProperty<Reference> = bind(autocommit = true) { property.select { it.referenceProperty } }
}
