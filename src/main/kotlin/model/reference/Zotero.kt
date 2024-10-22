package main.kotlin.model.reference

import org.jetbrains.exposed.sql.Table

val ZOTERO_VERSION = "5.0.89"

val FIELD_TITLE = "title"
val ABSTRACT_NOTE = "abstractNote"

val IGNORED_TYPES = setOf("attachment", "note")

// Authors
object Creators : Table() {
    val creatorId = integer("creatorID")
    val firstName = text("firstName")
    val lastName = text("lastName")
    override val primaryKey = PrimaryKey(creatorId, name = "creatorID")
}

// Mapping from Items to creators
object ItemCreators : Table() {
    val itemId = integer("itemID")
    val creatorId = integer("creatorID")
    val creatorTypeId = integer("creatorTypeID")
    override val primaryKey = PrimaryKey(itemId, name = "itemID")
}

// List of actual items in the Zotero application
object Items : Table() {
    val itemId = integer("itemID")
    val itemTypeId = integer("itemTypeID")
    val library = integer("libraryID")
    override val primaryKey = PrimaryKey(itemId, name = "itemID")
}

// Mapping from Item Type ID to Item type
object ItemTypes : Table() {
    val itemTypeId = integer("itemTypeID")
    val typeName = text("typeName")
    override val primaryKey = PrimaryKey(itemTypeId, name = "itemTypeID")
}

// Locations of PDFs
object ItemAttachments : Table() {
    val itemId = integer("itemID")
    val path = text("path")
    override val primaryKey = PrimaryKey(itemId, name = "itemId")
}

// Mapping from field to field name/type
object Fields : Table() {
    val fieldId = integer("fieldID")
    val fieldName = text("fieldName")
    override val primaryKey = PrimaryKey(fieldId, name = "fieldID")
}

// Types of fields, i.e. webpage, image, pdf, etc.
object FieldTypes : Table() {
    val fieldTypeId = integer("fieldTypeID")
    val fieldType = text("fieldType")
    override val primaryKey = PrimaryKey(fieldTypeId, name = "fieldTypeID")
}

// Mapping from item and field pair to value identifier
object ItemData : Table() {
    val itemId = integer("itemID")
    val fieldId = integer("fieldId")
    val valueId = integer("valueId")
    override val primaryKey = PrimaryKey(itemId, name = "itemID")
}

// Mapping from value identifier to actual value
object ItemDataValues : Table() {
    val valueId = integer("valueID")
    val value = text("value")
    override val primaryKey = PrimaryKey(valueId, name = "valueID")
}

// All libraries, useful for categorization
object Libraries : Table() {
    val libraryId = integer("libraryID")
    override val primaryKey = PrimaryKey(libraryId, name = "libraryID")
}
