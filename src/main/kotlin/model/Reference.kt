package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.select
import java.io.File

@Serializable(with = ReferenceSerializer::class)
class Reference(title: String, authors: List<Author>) {
    val titleProperty = SimpleStringProperty(title)
    val authorsProperty = SimpleListProperty(authors.asObservable())
    val pdfLocation = SimpleObjectProperty<File>()

    override fun equals(other: Any?): Boolean = other is Reference && titleProperty.get() == other.titleProperty.get()
            && authorsProperty.toList() == other.authorsProperty.toList()

    override fun hashCode(): Int = 31 * titleProperty.hashCode() + authorsProperty.hashCode()
}

class ReferenceModel(property: ObjectProperty<Reference>) : ItemViewModel<Reference>(itemProperty = property) {
    val title = bind(autocommit = true) { property.select { it.titleProperty } }
    val authors = bind(autocommit = true) { property.select { it.authorsProperty } }
}

/**
 * Custom ReferenceSerializer to handle the ObjectProperty's.
 */
object ReferenceSerializer : KSerializer<Reference> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Reference") {
        element<String>("title")
        element<List<Author>>("authors")
    }

    override fun serialize(encoder: Encoder, value: Reference) = encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.titleProperty.get())
        encodeSerializableElement(descriptor, 1, ListSerializer(AuthorSerializer), value.authorsProperty.toList())
    }

    override fun deserialize(decoder: Decoder): Reference = decoder.decodeStructure(descriptor) {
        var (title, authors) = Pair("", mutableListOf<Author>())
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> title = decodeStringElement(descriptor, index)
                1 -> authors.addAll(decodeSerializableElement(descriptor, index, ListSerializer(AuthorSerializer)))
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }

        Reference(title, authors)
    }
}
