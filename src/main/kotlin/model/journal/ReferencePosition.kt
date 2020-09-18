package main.kotlin.model.journal

import javafx.beans.property.*
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
class ReferencePosition(
    val referenceId: Int,
    start: Int,
    end: Int,
    referenceType: ReferenceType = ReferenceType.HIGHLIGHT
) {
    val startProperty = SimpleIntegerProperty(start)
    val endProperty = SimpleIntegerProperty(end)

    val referenceProperty = SimpleObjectProperty<Reference>()
    val typeProperty = SimpleObjectProperty(referenceType)

    val journalEntryProperty = SimpleObjectProperty<JournalEntry>()

    /**
     * Set reference directly.
     *
     * Useful if all references have already been loaded.
     */
    constructor(
        reference: Reference,
        start: Int,
        end: Int,
        referenceType: ReferenceType = ReferenceType.HIGHLIGHT
    ) : this(reference.id, start, end, referenceType) {
        referenceProperty.set(reference)
    }

    /**
     * Load reference based on mapping from identifier to actual reference.
     */
    fun loadReference(referenceMapping: Map<Int, Reference>, journalEntry: JournalEntry) {
        referenceProperty.set(referenceMapping[referenceId])
        journalEntryProperty.set(journalEntry)
    }

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
        element<Int>("reference")
        element<Int>("type")
    }

    override fun serialize(encoder: Encoder, value: ReferencePosition) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.startProperty.get())
        encodeIntElement(descriptor, 1, value.endProperty.get())
        encodeIntElement(descriptor, 2, value.referenceId)
        encodeIntElement(descriptor, 3, value.typeProperty.get().ordinal)
    }

    override fun deserialize(decoder: Decoder): ReferencePosition = decoder.decodeStructure(descriptor) {
        var (start, end) = Pair(0, 0)
        var referenceId = -1
        var referenceType = ReferenceType.HIGHLIGHT

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> start = decodeIntElement(descriptor, 0)
                1 -> end = decodeIntElement(descriptor, 1)
                2 -> referenceId = decodeIntElement(descriptor, 2)
                3 -> referenceType = ReferenceType.values()[decodeIntElement(descriptor, 3)]
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }

        ReferencePosition(referenceId, start, end, referenceType)
    }
}

enum class ReferenceType(val prettyName: String) {
    HIGHLIGHT("Highlight"),
    SUMMARY("Summary")
}

class ReferencePositionModel(property: ObjectProperty<ReferencePosition>) :
    ItemViewModel<ReferencePosition>(itemProperty = property) {
    val start = bind(autocommit = true) { property.select { it.startProperty.asString() } }
    val end = bind(autocommit = true) { property.select { it.endProperty.asString() } }
    val reference: ObjectProperty<Reference> = bind(autocommit = true) { property.select { it.referenceProperty } }
    val type =
        bind(autocommit = true) { property.select { it.typeProperty.select { SimpleStringProperty(it.prettyName) } } }
    val journalEntry: Property<JournalEntry> = bind(autocommit = true) { property.select { it.journalEntryProperty } }
}
