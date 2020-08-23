package main.kotlin.model

import javafx.beans.property.*
import javafx.collections.ObservableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.onChange
import tornadofx.select
import java.time.LocalDateTime
import java.time.ZoneOffset

@Serializable(with = JournalEntrySerializer::class)
class JournalEntry(
    creation: LocalDateTime = LocalDateTime.now(),
    lastEdit: LocalDateTime = LocalDateTime.now(),
    title: String = "",
    text: String = "",
    references: List<Reference> = listOf(),
    keywords: List<Keyword> = listOf()
) {
    val lastEditProperty = SimpleObjectProperty(lastEdit)
    val creationProperty = SimpleObjectProperty(creation)

    val editedProperty: BooleanProperty = SimpleBooleanProperty(false)

    val titleProperty = SimpleStringProperty(title)
    val textProperty = SimpleStringProperty(text)

    val referencesProperty = SimpleListProperty(references.toMutableList().asObservable())
    val keywordsProperty = SimpleListProperty(keywords.toMutableList().asObservable())

    init {
        titleProperty.onChange { editedProperty.set(true) }
        textProperty.onChange { editedProperty.set(true) }
        keywordsProperty.onChange<ObservableList<Keyword>> {
            it?.forEach { keyword -> keyword.editedProperty.onChange { editedProperty.set(editedProperty.get() || it) } }
            editedProperty.set(true)
        }

        keywordsProperty.forEach { keyword -> keyword.editedProperty.onChange { editedProperty.set(editedProperty.get() || it) } }
    }

    fun reset() {
        keywordsProperty.forEach { it.editedProperty.set(false) }
        editedProperty.set(false)
    }

    override fun equals(other: Any?): Boolean = other is JournalEntry
            && lastEditProperty.get().toEpochSecond(ZoneOffset.UTC) == other.lastEditProperty.get()
        .toEpochSecond(ZoneOffset.UTC)
            && creationProperty.get().toEpochSecond(ZoneOffset.UTC) == other.creationProperty.get()
        .toEpochSecond(ZoneOffset.UTC)
            && editedProperty.get() == other.editedProperty.get()
            && titleProperty.get() == other.titleProperty.get()
            && textProperty.get() == other.textProperty.get()
            && referencesProperty.get().toList() == other.referencesProperty.get().toList()
            && keywordsProperty.get().toList() == other.keywordsProperty.get().toList()

    override fun hashCode(): Int {
        var result = lastEditProperty.get().toEpochSecond(ZoneOffset.UTC).hashCode()
        result = 31 * result + creationProperty.get().toEpochSecond(ZoneOffset.UTC).hashCode()
        result = 31 * result + titleProperty.hashCode()
        result = 31 * result + textProperty.hashCode()
        result = 31 * result + referencesProperty.hashCode()
        result = 31 * result + keywordsProperty.hashCode()
        return result
    }
}


/**
 * Custom JournalEntrySerializer to handle the ObjectProperty's.
 */
object JournalEntrySerializer : KSerializer<JournalEntry> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("JournalEntry") {
        element<Long>("creation")
        element<Long>("last_edit")
        element<String>("title")
        element<String>("text")
        element<List<String>>("keywords")
        element<List<Reference>>("references")
    }

    override fun serialize(encoder: Encoder, value: JournalEntry) = encoder.encodeStructure(descriptor) {
        encodeLongElement(descriptor, 0, value.creationProperty.get().toEpochSecond(ZoneOffset.UTC))
        encodeLongElement(descriptor, 1, value.lastEditProperty.get().toEpochSecond(ZoneOffset.UTC))
        encodeStringElement(descriptor, 2, value.titleProperty.get())
        encodeStringElement(descriptor, 3, value.textProperty.get())
        encodeSerializableElement(descriptor, 4, ListSerializer(KeywordSerializer), value.keywordsProperty.toList())
        encodeSerializableElement(descriptor, 5, ListSerializer(ReferenceSerializer), value.referencesProperty.toList())
    }

    override fun deserialize(decoder: Decoder): JournalEntry = decoder.decodeStructure(descriptor) {
        var (creation, lastEdit) = Pair(LocalDateTime.now(), LocalDateTime.now())
        var (title, text) = Pair("", "")
        val (references, keywords) = Pair(mutableListOf<Reference>(), mutableListOf<Keyword>())

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> creation =
                    LocalDateTime.ofEpochSecond(decodeLongElement(descriptor, index) * 1000, 0, ZoneOffset.UTC)
                1 -> lastEdit =
                    LocalDateTime.ofEpochSecond(decodeLongElement(descriptor, index) * 1000, 0, ZoneOffset.UTC)
                2 -> title = decodeStringElement(descriptor, index)
                3 -> text = decodeStringElement(descriptor, index)
                4 -> keywords.addAll(decodeSerializableElement(descriptor, index, ListSerializer(KeywordSerializer)))
                5 -> references.addAll(
                    decodeSerializableElement(
                        descriptor,
                        index,
                        ListSerializer(ReferenceSerializer)
                    )
                )
                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unknown index $index")
            }
        }

        JournalEntry(creation, lastEdit, title, text, references, keywords)
    }
}

class JournalEntryModel(property: ObjectProperty<JournalEntry>) :
    ItemViewModel<JournalEntry>(itemProperty = property) {
    val title = bind(autocommit = true) { property.select { it.titleProperty } }
    val text = bind(autocommit = true) { property.select { it.textProperty } }
    val edited = bind(autocommit = true) { property.select { it.editedProperty } }
    val lastEdit = bind(autocommit = true) { property.select { it.lastEditProperty } }
    val creation = bind(autocommit = true) { property.select { it.creationProperty } }
    val keywords = bind(autocommit = true) { property.select { it.keywordsProperty } }
}
