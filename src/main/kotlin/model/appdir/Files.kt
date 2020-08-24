package main.kotlin.model.appdir

import com.moandjiezana.toml.Toml
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.toObservable
import java.io.File
import java.nio.file.Path

private val MAX_RECENT = 10

// Escape characters do not work well in TOML
private val SEPARATOR = "/"

class Files(opened: List<Path> = listOf()) : Tomable<Files> {
    val lastOpened = SimpleObjectProperty<Path>(if (opened.isEmpty()) null else opened.first())
    val recentFiles = SimpleListProperty(opened.toMutableList().toObservable())

    companion object {
        fun fromToml(toml: Toml): Files {
            val opened = toml.getList<String>("opened").map { pathString -> File(pathString).toPath() }
            return Files(opened)
        }
    }

    fun addLocation(location: File) {
        val newLocations = recentFiles.toMutableList()

        if (newLocations.isNotEmpty() && newLocations.first() != location.toPath() && location.exists()) {
            newLocations.add(0, location.toPath())
        } else if (newLocations.isEmpty() && location.exists()) {
            newLocations.add(location.toPath())
        }

        newLocations.dropLastWhile { newLocations.size > MAX_RECENT }
        this.recentFiles.setAll(newLocations)
    }

    override fun toTomlMap(): Map<String, *> {
        val map = HashMap<String, List<String>>()
        map["opened"] = recentFiles.toList().map { it.toString().replace("\\", SEPARATOR) }
        return map
    }
}
