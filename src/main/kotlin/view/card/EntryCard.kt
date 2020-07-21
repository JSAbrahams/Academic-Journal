package main.kotlin.view.card

import main.kotlin.controller.card.EntryController
import tornadofx.Fragment
import tornadofx.gridpane

class EntryCard : Fragment() {
    val entryController: EntryController by inject()

    override val root = gridpane {

    }
}
