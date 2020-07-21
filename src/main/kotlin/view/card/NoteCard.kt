package main.kotlin.view.card

import main.kotlin.controller.card.NoteController
import tornadofx.Fragment
import tornadofx.gridpane

class NoteCard : Fragment() {
    val noteController: NoteController by inject()

    override val root = gridpane {

    }
}
