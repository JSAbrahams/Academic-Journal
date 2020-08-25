package main.kotlin.controller

import com.moandjiezana.toml.TomlWriter
import javafx.beans.property.SimpleObjectProperty
import main.kotlin.model.appdir.Files
import tornadofx.Controller
import tornadofx.onChange
import java.io.File

private val FILES = "files.toml"

class AppdirController : Controller() {
    val storeController: StoreController by inject()

    val appdir = SimpleObjectProperty<File>()

    private var filesFile: File? = null

    val files = SimpleObjectProperty(Files())
    var writeFilesOnClose = true

    init {
        appdir.onChange {
            it?.mkdirs()
            if (it != null) setup(it)
        }

        storeController.location.onChange { if (it != null) files.get().addLocation(it) }
    }

    private fun setup(file: File) {
        filesFile = File(file, FILES)
        if (!filesFile!!.exists()) filesFile!!.createNewFile()

        if (filesFile != null && filesFile!!.exists()) {
            val fromToml = Files.fromToml(filesFile!!)
            writeFilesOnClose = fromToml.first
            files.set(fromToml.second)

            if (files.get().lastOpened.isNotNull.get() && storeController.location.isNull.get()) {
                storeController.loadJournal(files.get().lastOpened.get().toFile())
            }
        }
    }

    fun writeToFile() {
        val tomlWriter = TomlWriter()

        if (writeFilesOnClose && files.isNotNull.get() && filesFile != null && filesFile!!.exists()) {
            tomlWriter.write(files.get().toTomlMap(), filesFile)
        }
    }
}
