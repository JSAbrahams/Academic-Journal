package main.kotlin.model.appdir

interface Tomable<T> {
    fun toTomlMap(): Map<String, *>
}
