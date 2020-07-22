package main.kotlin.model

import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty

abstract class Reference(start: IntegerProperty, end: IntegerProperty, note: StringProperty?)

class BibtexReference(
    val start: IntegerProperty,
    val end: IntegerProperty,
    val note: StringProperty?,
    val bibtex: ObjectProperty<Bibtex>
) : Reference(start, end, note)

class Bibtex
