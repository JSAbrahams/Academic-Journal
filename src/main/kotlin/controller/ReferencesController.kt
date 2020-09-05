package main.kotlin.controller

import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import main.kotlin.model.journal.ReferenceType
import main.kotlin.model.reference.*
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
    val authorMapping = SimpleMapProperty<Int, Author>()
    val subcollectionMapping = SimpleMapProperty<Int, String>()
    val referenceMapping = SimpleMapProperty<Int, Reference>()
    val selectedReference = SimpleObjectProperty<Reference>()

    val selectedType = SimpleObjectProperty(ReferenceType.HIGHLIGHT)
    val typeColors = SimpleMapProperty(
        mapOf(
            ReferenceType.HIGHLIGHT to Color.YELLOW,
            ReferenceType.SUMMARY to Color.web("99cccc")
        ).asObservable()
    )

    init {
        referenceMapping.onChange {
            if (journalController.journalProperty.isNotNull.get()) {
                journalController.journalProperty.get().loadReference(referenceMapping.toMap())
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

        transaction {

            Items.selectAll().forEach { result ->
                val itemType = ItemTypes
                    .select { ItemTypes.itemTypeId eq result[Items.itemTypeId] }
                    .firstOrNull()?.get(ItemTypes.typeName) ?: ""

                val field = { fieldType: String ->
                    val abstractValueId = ItemData
                        .select { ItemData.itemId eq result[Items.itemId] and (ItemData.fieldId eq fieldTypes[fieldType]!!) }
                        .firstOrNull()?.get(ItemData.valueId) ?: -1
                    ItemDataValues
                        .select { ItemDataValues.valueId eq abstractValueId }
                        .firstOrNull()?.get(ItemDataValues.value) ?: ""
                }

                if (!IGNORED_TYPES.contains(itemType)) referenceMapping[result[Items.itemId]] = Reference(
                    id = result[Items.itemId],
                    itemType = itemType,
                    title = field(FIELD_TITLE),
                    authors = ItemCreators.select { ItemCreators.itemId eq result[Items.itemId] }.flatMap {
                        Creators.select { Creators.creatorId eq it[ItemCreators.creatorId] }.map { creator ->
                            Author(creator[Creators.firstName], creator[Creators.lastName])
                        }
                    },
                    abstract = field(ABSTRACT_NOTE)
                )
            }

            Creators.selectAll().forEach {
                authorMapping[it[Creators.creatorId]] = Author(it[Creators.firstName], it[Creators.lastName])
            }
        }

        this.authorMapping.set(authorMapping.toObservable())
        this.referenceMapping.set(referenceMapping.toObservable())
        lastSync.set(LocalDateTime.now())
    }
}
