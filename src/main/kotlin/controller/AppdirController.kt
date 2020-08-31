package main.kotlin.controller

import com.moandjiezana.toml.TomlWriter
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import main.kotlin.model.appdir.Settings
import main.kotlin.model.journal.Journal
import main.kotlin.model.journal.JournalMeta
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.onChange
import java.io.File

private val FILES = "files.toml"

class AppdirController : Controller() {
    val journalController: JournalController by inject()

    val appdir = SimpleObjectProperty<File>()
    val journalDir = SimpleObjectProperty<File>()

    val allJournals = SimpleListProperty(mutableListOf<JournalMeta>().asObservable())
    val recentJournals = SimpleListProperty(mutableListOf<JournalMeta>().asObservable())

    private var settingsFile: File? = null
    private var settings: Settings? = null

    init {
        appdir.onChange {
            if (it == null) return@onChange

            it.mkdirs()
            val journals = File(it, "json")
            journals.mkdirs()
            journalDir.set(journals)

            setup(it)

            val meta = journals.listFiles()?.map { file -> file to JournalMeta.load(file) }?.toMap() ?: emptyMap()
            allJournals.addAll(meta.values)
            recentJournals.addAll(settings?.recentFiles?.map { meta[it] }?.filterNotNull() ?: emptyList())
        }

        journalController.location.onChange { if (it != null) settings?.addLocation(it) }
    }

    /**
     * Create new journal with given title and fileName in journal directory and return location
     */
    fun newJournal(title: String, fileName: String): File {
        if (journalDir.isNull.get()) throw IllegalStateException("Journal Directory must be set")

        val file = File(journalDir.value, fileName)
        val journal = Journal(title = title)
        val journalMeta = JournalMeta.from(journal, file)
        allJournals.add(journalMeta)
        recentJournals.add(journalMeta)

        file.writeText(Json.encodeToString(journal))
        return file
    }

    private fun setup(file: File) {
        settingsFile = File(file, FILES)
        if (!settingsFile!!.exists()) settingsFile!!.createNewFile()

        val fromToml = Settings.fromToml(settingsFile!!)
        settings = fromToml.second

        if (settings!!.lastOpened.isNotNull.get() && journalController.location.isNull.get()) {
            journalController.loadJournal(settings!!.lastOpened.get().toFile())
        }
    }

    fun writeToFile() {
        val tomlWriter = TomlWriter()
        if (settings == null) return
        settings!!.recentFiles.clear()
        settings!!.recentFiles.addAll(recentJournals.map { if (it.fileProperty.isNull.get()) null else it.fileProperty.value.toPath() })

        if (settingsFile != null && settingsFile!!.exists()) {
            tomlWriter.write(settings!!.toTomlMap(), settingsFile)
        }
    }

    /**
     * Store journal meta data for later use.
     */
    fun loadedJournal(journal: Journal, file: File) = recentJournals.add(JournalMeta.from(journal, file))
}
