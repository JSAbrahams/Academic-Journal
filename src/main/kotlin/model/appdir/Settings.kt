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

class Settings(opened: List<Path> = listOf()) {
    val lastOpened = SimpleObjectProperty<Path>(if (opened.isEmpty()) null else opened.first())
    val recentFiles = SimpleListProperty(opened.toMutableList().toObservable())

    companion object {
        /**
         * Read `*.toml` file and extract items.
         *
         * If exception, then log this exception and return false if this is not a NullPointerException,
         * which is allowed on first boot.
         */
        fun fromToml(file: File): Pair<Boolean, Settings> = try {
            val toml = Toml().read(file)
            val opened = toml.getList<String>("opened").map { pathString -> File(pathString).toPath() }
            Pair(true, Settings(opened))
        } catch (e: Exception) {
            // TODO log exception
            // NullpointerException is allowed on first boot for now
            Pair(e !is NullPointerException, Settings(emptyList()))
        }
    }

    fun addLocation(location: File) {
        val newLocations = recentFiles.toMutableList()

        // Remove all current locations to prevent duplication
        newLocations.removeAll { it == location.toPath() }
        if (newLocations.isEmpty()) {
            newLocations.add(location.toPath())
        } else {
            newLocations.add(0, location.toPath())
        }

        newLocations.dropLastWhile { newLocations.size > MAX_RECENT }
        this.recentFiles.setAll(newLocations)
    }

    fun toTomlMap(): Map<String, *> {
        val map = HashMap<String, List<String>>()
        map["opened"] = recentFiles.toList().map { it.toString().replace("\\", SEPARATOR) }
        return map
    }
}
