package main.kotlin.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.select

@Serializable(with = AuthorSerializer::class)
class Author(names: List<String>) {
    val firstProperty = SimpleStringProperty(if (names.isNotEmpty()) names[0] else "")
    val surnameProperty = SimpleStringProperty(if (names.size > 1) names.last() else "")
    val namesProperty = SimpleListProperty(names.asObservable())
}

/**
 * Custom AuthorSerializer to handle the ObjectProperty's.
 */
object AuthorSerializer : KSerializer<Author> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Note") {
        element<Array<String>>("names")
    }

    override fun serialize(encoder: Encoder, value: Author) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, ListSerializer(String.serializer()), value.namesProperty.toList())
    }

    override fun deserialize(decoder: Decoder): Author = decoder.decodeStructure(descriptor) {
        val names = mutableListOf<String>()
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> names.addAll(decodeSerializableElement(descriptor, index, ListSerializer(String.serializer())))
                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unknown index $index")
            }
        }
        Author(names)
    }
}

class AuthorModel(property: ObjectProperty<Author>) : ItemViewModel<Author>(itemProperty = property) {
    val start = bind(autocommit = true) { property.select { it.firstProperty } }
    val surname = bind(autocommit = true) { property.select { it.surnameProperty } }
    val names = bind(autocommit = true) { property.select { it.namesProperty } }
}
