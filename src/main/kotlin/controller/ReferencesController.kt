package main.kotlin.controller

import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.reference.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.Controller
import tornadofx.toObservable
import java.io.File
import java.time.LocalDateTime

class ReferencesController : Controller() {
    val storeController: StoreController by inject()

    // Hardcode for now
    val zoteroDirectory = File(System.getProperty("user.home"), "Zotero")
    val location = SimpleObjectProperty(File(zoteroDirectory, "zotero.sqlite"))
    var connected = false

    val lastSync = SimpleObjectProperty(LocalDateTime.now())

    fun connect() {
        Database.connect(url = File("jdbc:sqlite:", location.get().absolutePath).path, driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Creators, Items, ItemAttachments, Fields, ItemData, ItemDataValues, Libraries)
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
            Items.selectAll().forEach {
                referenceMapping[it[Items.id]] = Reference()
            }

            Creators.selectAll().forEach {
                authorMapping[it[Creators.id]] = Author(it[Creators.firstName], it[Creators.lastName])
            }
        }

        storeController.authorMapping.set(authorMapping.toObservable())
        storeController.referenceMapping.set(referenceMapping.toObservable())
        lastSync.set(LocalDateTime.now())
    }
}
