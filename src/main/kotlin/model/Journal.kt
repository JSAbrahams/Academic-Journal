package main.kotlin.model

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.*
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.SetSerializer
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
class Journal(title: String = "", items: List<JournalEntry> = listOf(), tags: Set<Tag> = setOf()) {
    val titleProperty = SimpleStringProperty(title)

    private val myItems = items.toMutableList().asObservable()
    val itemsProperty: ReadOnlyListProperty<JournalEntry> = SimpleListProperty(myItems)

    var editedProperty = SimpleBooleanProperty(false)

    private val myKeywords = tags.toMutableSet().asObservable()
    val tags: ObservableSet<Tag> = SimpleSetProperty(myKeywords)
    val keywordList = SimpleListProperty(mutableListOf<Tag>().asObservable())

    companion object {
        fun load(file: File): Journal = Json.decodeFromString(file.readText())
    }

    init {
        keywordList.addAll(tags)
        this.tags.addListener { c: SetChangeListener.Change<out Tag> ->
            if (c.wasAdded()) keywordList.add(c.elementAdded)
            if (c.wasRemoved()) keywordList.add(c.elementRemoved)
        }

        itemsProperty.forEach { item -> item.loadKeywords(tags.map { it.id to it }.toMap()) }
        resetEdited()
    }

    fun reset() = itemsProperty.forEach { it.reset() }

    /**
     * Load reference based on mapping from identifier to actual reference.
     */
    fun loadReference(referenceMapping: Map<Int, Reference>) {
        itemsProperty.forEach { it.loadReference(referenceMapping) }
    }

    fun addKeyword(tag: Tag) {
        tags.add(tag)
        resetEdited()
    }

    fun addJournalEntry(journalEntry: JournalEntry) {
        myItems.add(journalEntry)
        resetEdited()
    }

    private fun resetEdited() {
        val or = tags.fold(
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
        element<Set<Tag>>("tags")
        element<List<JournalEntry>>("entries")
    }

    override fun serialize(encoder: Encoder, value: Journal) = encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.titleProperty.get())
        encodeSerializableElement(descriptor, 1, SetSerializer((KeywordSerializer)), value.tags.toSet())
        encodeSerializableElement(descriptor, 2, ListSerializer(JournalEntrySerializer), value.itemsProperty.toList())
    }

    override fun deserialize(decoder: Decoder): Journal = decoder.decodeStructure(descriptor) {
        var title = ""
        val (keywords, entries) = Pair(mutableSetOf<Tag>(), mutableListOf<JournalEntry>())
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> title = decodeStringElement(descriptor, 0)
                1 -> keywords.addAll(decodeSerializableElement(descriptor, 1, SetSerializer(KeywordSerializer)))
                2 -> entries.addAll(decodeSerializableElement(descriptor, 2, ListSerializer(JournalEntrySerializer)))
                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unknown index $index")
            }
        }

        Journal(title, entries, keywords)
    }
}
