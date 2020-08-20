package main.kotlin.model

import javafx.beans.property.*
import javafx.collections.ObservableList
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
import tornadofx.onChange
import tornadofx.select
import java.util.*

@Serializable(with = JournalEntrySerializer::class)
class JournalEntry(
    creation: Date = Date(),
    lastEdit: Date = Date(),
    title: String = "",
    text: String = "",
    notes: List<Note> = listOf(),
    keywords: List<String> = listOf()
) {
    val lastEditProperty = SimpleObjectProperty(lastEdit)
    val creationProperty = SimpleObjectProperty(creation)

    val editedProperty: BooleanProperty = SimpleBooleanProperty(true)

    val titleProperty = SimpleStringProperty(title)
    val textProperty = SimpleStringProperty(text)

    val notesProperty = SimpleListProperty(notes.asObservable())
    val keywordsProperty = SimpleListProperty(keywords.asObservable())

    init {
        titleProperty.onChange { editedProperty.set(true) }
        textProperty.onChange { editedProperty.set(true) }
        notesProperty.onChange<ObservableList<Note>> { editedProperty.set(true) }
        keywordsProperty.onChange<ObservableList<String>> { editedProperty.set(true) }
    }

    override fun equals(other: Any?): Boolean = other is JournalEntry
            && lastEditProperty.get().toInstant().epochSecond == other.lastEditProperty.get().toInstant().epochSecond
            && creationProperty.get().toInstant().epochSecond == other.creationProperty.get().toInstant().epochSecond
            && editedProperty.get() == other.editedProperty.get()
            && titleProperty.get() == other.titleProperty.get()
            && textProperty.get() == other.textProperty.get()
            && notesProperty.get().toList() == other.notesProperty.get().toList()
            && keywordsProperty.get().toList() == other.keywordsProperty.get().toList()

    override fun hashCode(): Int {
        var result = lastEditProperty.get().toInstant().epochSecond.hashCode()
        result = 31 * result + creationProperty.get().toInstant().epochSecond.hashCode()
        result = 31 * result + editedProperty.hashCode()
        result = 31 * result + titleProperty.hashCode()
        result = 31 * result + textProperty.hashCode()
        result = 31 * result + notesProperty.hashCode()
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
        element<Long>("last_edited")
        element<String>("title")
        element<String>("text")
        element<List<Note>>("notes")
        element<List<String>>("keywords")
    }

    override fun serialize(encoder: Encoder, value: JournalEntry) = encoder.encodeStructure(descriptor) {
        encodeLongElement(descriptor, 0, value.creationProperty.get().toInstant().epochSecond)
        encodeLongElement(descriptor, 1, value.lastEditProperty.get().toInstant().epochSecond)
        encodeStringElement(descriptor, 2, value.titleProperty.get())
        encodeStringElement(descriptor, 3, value.textProperty.get())
        encodeSerializableElement(descriptor, 4, ListSerializer(NoteSerializer), value.notesProperty.toList())
        encodeSerializableElement(descriptor, 5, ListSerializer(String.serializer()), value.keywordsProperty.toList())
    }

    override fun deserialize(decoder: Decoder): JournalEntry = decoder.decodeStructure(descriptor) {
        var lastEdit = Date()
        var creation = Date()
        var title = ""
        var text = ""
        val notes = mutableListOf<Note>()
        val keywords = mutableListOf<String>()
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> creation = Date(decodeLongElement(descriptor, index) * 1000)
                1 -> lastEdit = Date(decodeLongElement(descriptor, index) * 1000)
                2 -> title = decodeStringElement(descriptor, index)
                3 -> text = decodeStringElement(descriptor, index)
                4 -> notes.addAll(decodeSerializableElement(descriptor, index, ListSerializer(NoteSerializer)))
                5 -> keywords.addAll(decodeSerializableElement(descriptor, index, ListSerializer(String.serializer())))
                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unknown index $index")
            }
        }

        JournalEntry(creation, lastEdit, title, text, notes, keywords)
    }
}

class JournalEntryModel(property: ObjectProperty<JournalEntry>) :
    ItemViewModel<JournalEntry>(itemProperty = property) {
    val title = bind(autocommit = true) { property.select { it.titleProperty } }
    val text = bind(autocommit = true) { property.select { it.textProperty } }
    val edited = bind(autocommit = true) { property.select { it.editedProperty } }
    val lastEdit = bind(autocommit = true) { property.select { it.lastEditProperty } }
    val creation = bind(autocommit = true) { property.select { it.creationProperty } }
    val notes = bind(autocommit = true) { property.select { it.notesProperty } }
    val keywords = bind(autocommit = true) { property.select { it.keywordsProperty } }
}
