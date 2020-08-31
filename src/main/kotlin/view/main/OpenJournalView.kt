package main.kotlin.view.main

import main.kotlin.Styles
import main.kotlin.controller.AppdirController
import main.kotlin.controller.JournalController
import main.kotlin.model.journal.JournalMeta
import main.kotlin.model.journal.JournalMetaModel
import tornadofx.*

class OpenJournalView : View() {
    val journalController: JournalController by inject()
    val appdirController: AppdirController by inject()

    override val root = vbox {
        addClass(Styles.customContainer)

        text("Open") { addClass(Styles.title) }

        listview(appdirController.allJournals) {
            cellFragment(OpenJournalFragment::class)
        }
    }
}

class OpenJournalFragment : ListCellFragment<JournalMeta>() {
    val journalController: JournalController by inject()

    val entry = JournalMetaModel(itemProperty)

    override val root = vbox {
        text(entry.title)

        setOnMouseClicked {
            if (it.clickCount == 2) journalController.loadJournal(entry.file.value)
        }
    }
}
