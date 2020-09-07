package main.kotlin.controller

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.SetChangeListener
import javafx.scene.paint.Color
import main.kotlin.model.journal.ReferenceType
import main.kotlin.model.reference.*
import main.kotlin.model.reference.Collection
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.onChange
import java.io.File
import java.time.LocalDateTime
import kotlin.collections.set

class ReferencesController : Controller() {
    val journalController: JournalController by inject()

    // Hardcode for now
    val zoteroDirectory = File(System.getProperty("user.home"), "Zotero")
    val location = SimpleObjectProperty(File(zoteroDirectory, "zotero.sqlite"))
    var connected = false
    val lastSync = SimpleObjectProperty(LocalDateTime.now())

    private val fieldTypes = mutableMapOf<String, Int>()
    val authorsProperty = SimpleMapProperty<Int, Author>()
    val collectionsProperty = SimpleMapProperty<Int, Collection>()
    val referencesProperty = SimpleMapProperty<Int, Reference>()

    private val filteredReferences = mutableSetOf<Reference>().asObservable()
    val filteredReferencesProperty = SimpleListProperty(mutableListOf<Reference>().asObservable())

    val selectedReferenceProperty = SimpleObjectProperty<Reference>()

    val selectedType = SimpleObjectProperty(ReferenceType.HIGHLIGHT)
    val typeColors = SimpleMapProperty(
        mapOf(
            ReferenceType.HIGHLIGHT to Color.YELLOW,
            ReferenceType.SUMMARY to Color.web("99cccc")
        ).asObservable()
    )

    init {
        this.filteredReferences.addListener { c: SetChangeListener.Change<out Reference> ->
            if (c.wasAdded()) filteredReferencesProperty.add(c.elementAdded)
            if (c.wasRemoved()) filteredReferencesProperty.remove(c.elementRemoved)
        }

        authorsProperty.onChange {
            checkFilters()
            it?.forEach { _, author -> author.selectedProperty.onChange { checkFilters() } }
        }
        collectionsProperty.onChange {
            checkFilters()
            it?.forEach { _, collection -> collection.selectedProperty.onChange { checkFilters() } }
        }

        referencesProperty.onChange {
            if (journalController.journalProperty.isNotNull.get()) {
                journalController.journalProperty.get().loadReference(referencesProperty.toMap())
            }

            filteredReferences.addAll(it?.values?.toList() ?: emptyList())
            // Re-apply filters, in case we make selected properties non volatile
            checkFilters()
        }
    }

    private fun checkFilters() {
        filteredReferences.removeIf { reference ->
            val collectionUnselected = reference.collectionProperty.value?.selectedProperty?.not()?.get() ?: true
            val allAuthorUnselected = reference.authorProperty.value?.all { it.selectedProperty.not().get() } ?: true
            collectionUnselected || allAuthorUnselected
        }
        filteredReferences.addAll(referencesProperty.values.filter { reference ->
            val collectionSelected = reference.collectionProperty.value?.selectedProperty?.get() ?: false
            val anyAuthorSelected = reference.authorProperty.value?.any { it.selectedProperty.get() } ?: false
            collectionSelected && anyAuthorSelected
        })
    }

    fun connect() {
        Database.connect(url = File("jdbc:sqlite:", location.get().absolutePath).path, driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(
                Creators,
                ItemCreators,
                Items,
                ItemAttachments,
                Fields,
                ItemData,
                ItemDataValues,
                Libraries
            )

            fieldTypes[FIELD_TITLE] = Fields
                .select { Fields.fieldName eq FIELD_TITLE }
                .firstOrNull()?.get(Fields.fieldId) ?: -1
            fieldTypes[ABSTRACT_NOTE] = Fields
                .select { Fields.fieldName eq ABSTRACT_NOTE }
                .firstOrNull()?.get(Fields.fieldId) ?: -1
        }

        connected = true
    }

    /**
     * Refresh references and update these in the StoreController.
     * If not connected to a database, does nothing.
     */
    fun refreshReferences() {
        if (!connected) return

        transaction {
            authorsProperty.set(Creators.selectAll().map { creator ->
                creator[Creators.id] to Author(
                    creator[Creators.id],
                    creator[Creators.firstName],
                    creator[Creators.lastName]
                )
            }.toMap().asObservable())

            collectionsProperty.set(Collections.selectAll().map { collection ->
                collection[Collections.id] to Collection(collection[Collections.id], collection[Collections.name])
            }.toMap().asObservable())

            referencesProperty.set(Items.selectAll().map { result ->
                val itemType = ItemTypes
                    .select { ItemTypes.itemTypeId eq result[Items.itemTypeId] }
                    .firstOrNull()?.get(ItemTypes.typeName) ?: ""

                val field = { fieldType: String ->
                    val abstractValueId = ItemData
                        .select { ItemData.itemId eq result[Items.id] and (ItemData.fieldId eq fieldTypes[fieldType]!!) }
                        .firstOrNull()?.get(ItemData.valueId) ?: -1
                    ItemDataValues
                        .select { ItemDataValues.valueId eq abstractValueId }
                        .firstOrNull()?.get(ItemDataValues.value) ?: ""
                }

                val creatorIds: List<Int> =
                    ItemCreators.select { ItemCreators.itemId eq result[Items.id] }.map { it[ItemCreators.creatorId] }
                val collectionId: Int = CollectionItems
                    .select { CollectionItems.itemId eq result[Items.id] }
                    .firstOrNull()
                    ?.let { it[CollectionItems.collectionId] } ?: -1

                result[Items.id] to Reference(
                    id = result[Items.id],
                    itemType = itemType,
                    title = field(FIELD_TITLE),
                    authors = authorsProperty.values.filter { author -> creatorIds.contains(author.id) },
                    abstract = field(ABSTRACT_NOTE),
                    collection = collectionsProperty[collectionId]
                )
            }.toMap().asObservable())
        }

        lastSync.set(LocalDateTime.now())
    }
}
