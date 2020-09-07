package main.kotlin.controller

import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import main.kotlin.model.journal.ReferenceType
import main.kotlin.model.reference.*
import main.kotlin.model.reference.Collection
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.onChange
import tornadofx.toObservable
import java.io.File
import java.time.LocalDateTime

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
    val selectedReferenceProperty = SimpleObjectProperty<Reference>()

    val selectedType = SimpleObjectProperty(ReferenceType.HIGHLIGHT)
    val typeColors = SimpleMapProperty(
        mapOf(
            ReferenceType.HIGHLIGHT to Color.YELLOW,
            ReferenceType.SUMMARY to Color.web("99cccc")
        ).asObservable()
    )

    init {
        referencesProperty.onChange {
            if (journalController.journalProperty.isNotNull.get()) {
                journalController.journalProperty.get().loadReference(referencesProperty.toMap())
            }
        }
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

        val authorMapping = mutableMapOf<Int, Author>()
        val referenceMapping = mutableMapOf<Int, Reference>()
        val collectionMapping = mutableMapOf<Int, Collection>()

        transaction {
            Items.selectAll().forEach { result ->
                val itemType = ItemTypes
                    .select { ItemTypes.itemTypeId eq result[Items.itemTypeId] }
                    .firstOrNull()?.get(ItemTypes.typeName) ?: ""

                if (!IGNORED_TYPES.contains(itemType)) {
                    val field = { fieldType: String ->
                        val abstractValueId = ItemData
                            .select { ItemData.itemId eq result[Items.id] and (ItemData.fieldId eq fieldTypes[fieldType]!!) }
                            .firstOrNull()?.get(ItemData.valueId) ?: -1
                        ItemDataValues
                            .select { ItemDataValues.valueId eq abstractValueId }
                            .firstOrNull()?.get(ItemDataValues.value) ?: ""
                    }

                    val authors = ItemCreators.select { ItemCreators.itemId eq result[Items.id] }.flatMap {
                        Creators.select { Creators.id eq it[ItemCreators.id] }.map { creator ->
                            Author(creator[Creators.id], creator[Creators.firstName], creator[Creators.lastName])
                        }
                    }

                    val collectionItem =
                        CollectionItems.select { CollectionItems.itemId eq result[Items.id] }.firstOrNull()
                    val collection: Collection? = if (collectionItem != null) {
                        Collections.select { Collections.id eq collectionItem[CollectionItems.collectionId] }
                            .firstOrNull()
                            ?.let { Collection(it[Collections.id], it[Collections.name]) }
                    } else null

                    referenceMapping[result[Items.id]] = Reference(
                        id = result[Items.id],
                        itemType = itemType,
                        title = field(FIELD_TITLE),
                        authors = authors,
                        abstract = field(ABSTRACT_NOTE),
                        collection = collection
                    )

                    authors.forEach { authorMapping[it.id] = it }
                    if (collection != null && !collectionMapping.containsKey(collection.id)) {
                        collectionMapping[collection.id] = collection
                    }
                }
            }

            // Remaining authors
            Creators.selectAll().forEach {
                if (!authorMapping.containsKey(it[Creators.id]))
                    authorMapping[it[Creators.id]] =
                        Author(it[Creators.id], it[Creators.firstName], it[Creators.lastName])
            }
        }

        this.authorsProperty.set(authorMapping.toObservable())
        this.referencesProperty.set(referenceMapping.toObservable())
        this.collectionsProperty.set(collectionMapping.toObservable())
        lastSync.set(LocalDateTime.now())
    }
}
