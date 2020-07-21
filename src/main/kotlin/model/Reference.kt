package main.kotlin.model

abstract class Reference(start: Int, end: Int, note: String?)

class BibtexReference(val start: Int, val end: Int, val note: String?, val bibtex: Bibtex)

class Bibtex
