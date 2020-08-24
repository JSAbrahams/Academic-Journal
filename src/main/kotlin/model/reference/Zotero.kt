package main.kotlin.model.reference

import org.jetbrains.exposed.sql.Table

// Authors
object Creators : Table() {
    val id = integer("creatorID")
    val firstName = text("firstName")
    val lastName = text("lastName")
    override val primaryKey = PrimaryKey(id, name = "creatorID")
}

// List of actual items in the Zotero application
object Items : Table() {
    val id = integer("itemID")
    val itemTypeId = integer("itemTypeID")
    val library = integer("libraryID")
    override val primaryKey = PrimaryKey(id, name = "itemID")
}

// Locations of PDFs
object ItemAttachments : Table() {
    val id = integer("itemID")
    val path = text("path")
    override val primaryKey = PrimaryKey(id, name = "itemId")
}

// Mapping from field to field name/type
object Fields : Table() {
    val id = integer("fieldID")
    val name = text("fieldName")
    override val primaryKey = PrimaryKey(id, name = "fieldID")
}

// Mapping from item and field pair to value identifier
object ItemData : Table() {
    val id = integer("itemID")
    val field = integer("fieldId")
    val value = integer("valueId")
    override val primaryKey = PrimaryKey(id, name = "itemID")
}

// Mapping from value identifier to actual value
object ItemDataValues : Table() {
    val id = integer("valueID")
    val value = text("value")
    override val primaryKey = PrimaryKey(id, name = "valueID")
}

// All libraries, useful for categorization
object Libraries : Table() {
    val id = integer("libraryID")
    override val primaryKey = PrimaryKey(id, name = "libraryID")
}
