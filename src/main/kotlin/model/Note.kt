package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.select

@Serializable(with = NoteSerializer::class)
class Note(start: Int, end: Int, note: String = "", reference: List<Reference> = mutableListOf()) {
    val startProperty = SimpleIntegerProperty(start)
    val endProperty = SimpleIntegerProperty(end)

    val noteProperty = SimpleStringProperty(note)
    val referencesProperty = SimpleListProperty(reference.asObservable())

    override fun equals(other: Any?): Boolean = other is Note && startProperty.get() == other.startProperty.get()
            && endProperty.get() == other.endProperty.get() && noteProperty.get() == other.noteProperty.get()
            && referencesProperty.get() == other.referencesProperty.get()

    override fun hashCode(): Int {
        var result = startProperty.hashCode()
        result = 31 * result + endProperty.hashCode()
        result = 31 * result + noteProperty.hashCode()
        result = 31 * result + referencesProperty.hashCode()
        return result
    }
}

/**
 * Custom NoteSerializer to handle the ObjectProperty's.
 */
object NoteSerializer : KSerializer<Note> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Note") {
        element<Int>("start")
        element<Int>("end")
        element<String>("note")
    }

    override fun serialize(encoder: Encoder, value: Note) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.startProperty.get())
        encodeIntElement(descriptor, 1, value.endProperty.get())
        encodeStringElement(descriptor, 2, value.noteProperty.get())
    }

    override fun deserialize(decoder: Decoder): Note = decoder.decodeStructure(descriptor) {
        var start = -1
        var end = -1
        var note = ""
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> start = decodeIntElement(descriptor, 0)
                1 -> end = decodeIntElement(descriptor, 1)
                2 -> note = decodeStringElement(descriptor, 2)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        Note(start, end, note)
    }
}

class NoteModel(property: ObjectProperty<Note>) : ItemViewModel<Note>(itemProperty = property) {
    val start = bind(autocommit = true) { property.select { it.startProperty } }
    val end = bind(autocommit = true) { property.select { it.endProperty } }
    val note = bind(autocommit = true) { property.select { it.noteProperty } }
    val references = bind(autocommit = true) { property.select { it.referencesProperty } }
}
