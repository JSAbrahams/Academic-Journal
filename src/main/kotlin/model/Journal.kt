package main.kotlin.model

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import main.kotlin.model.reference.Reference
import tornadofx.asObservable
import tornadofx.cleanBind
import java.io.File

@Serializable(with = JournalSerializer::class)
class Journal(title: String = "", items: List<JournalEntry> = listOf(), keywords: List<Keyword> = listOf()) {
    val titleProperty = SimpleStringProperty(title)

    private val myItems = items.toMutableList().asObservable()
    val itemsProperty: ReadOnlyListProperty<JournalEntry> = SimpleListProperty(myItems)

    var editedProperty = SimpleBooleanProperty(false)

    private val myKeywords = keywords.toMutableList().asObservable()
    val keywords: SimpleListProperty<Keyword> = SimpleListProperty(myKeywords)

    companion object {
        fun load(file: File): Journal = Json.decodeFromString(file.readText())
    }

    init {
        itemsProperty.forEach { item -> item.loadKeywords(keywords.map { it.textProperty.get() to it }.toMap()) }
        resetEdited()
    }

    fun reset() = itemsProperty.forEach { it.reset() }

    /**
     * Load reference based on mapping from identifier to actual reference.
     */
    fun loadReference(referenceMapping: Map<Int, Reference>) {
        itemsProperty.forEach { it.loadReference(referenceMapping) }
    }

    fun addKeyword(keyword: Keyword) {
        keywords.add(keyword)
        resetEdited()
    }

    fun addJournalEntry(journalEntry: JournalEntry) {
        myItems.add(journalEntry)
        resetEdited()
    }

    private fun resetEdited() {
        val or = keywords.fold(
            myItems.fold(
                SimpleBooleanProperty(false),
                fun(acc, b): BooleanBinding { return Bindings.or(acc, b.editedProperty) }),
            fun(acc, b): BooleanBinding { return Bindings.or(acc, b.editedProperty) })

        editedProperty.unbind()
        editedProperty.set(true)
        editedProperty.cleanBind(or)
    }

    override fun equals(other: Any?): Boolean = other is Journal && titleProperty.get() == other.titleProperty.get()
            && itemsProperty.toList() == other.itemsProperty.toList()

    override fun hashCode(): Int = 31 * titleProperty.hashCode() + itemsProperty.hashCode()
}

/**
 * Custom ReferenceSerializer to handle the ObjectProperty's.
 */
object JournalSerializer : KSerializer<Journal> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Journal") {
        element<String>("title")
        element<List<Keyword>>("keywords")
        element<List<JournalEntry>>("entries")
    }

    override fun serialize(encoder: Encoder, value: Journal) = encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.titleProperty.get())
        encodeSerializableElement(descriptor, 1, ListSerializer((KeywordSerializer)), value.keywords.get())
        encodeSerializableElement(descriptor, 2, ListSerializer(JournalEntrySerializer), value.itemsProperty.toList())
    }

    override fun deserialize(decoder: Decoder): Journal = decoder.decodeStructure(descriptor) {
        var title = ""
        val (keywords, entries) = Pair(mutableListOf<Keyword>(), mutableListOf<JournalEntry>())
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> title = decodeStringElement(descriptor, 0)
                1 -> keywords.addAll(decodeSerializableElement(descriptor, 1, ListSerializer(KeywordSerializer)))
                2 -> entries.addAll(decodeSerializableElement(descriptor, 2, ListSerializer(JournalEntrySerializer)))
                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unknown index $index")
            }
        }

        Journal(title, entries, keywords)
    }
}
