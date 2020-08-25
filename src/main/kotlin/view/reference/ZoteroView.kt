package main.kotlin.view.reference

import main.kotlin.controller.ReferencesController
import tornadofx.*

class ZoteroView : View() {
    val referencesController: ReferencesController by inject()

    override val root = vbox {
        hbox {
            text("Database: ")
            text(referencesController.location.asString())
        }

        hbox {
            button("Resync").action {
                referencesController.refreshReferences()
            }
            text("Last sync: ")
            text(referencesController.lastSync.asString())
        }

        hbox {
            vbox {
                text("Authors")
                listview(observableListOf(referencesController.authorMapping.values)) {
                    cellFragment(AuthorFragment::class)
                }
            }

            vbox {
                text("Items")
                listview(observableListOf(referencesController.referenceMapping.values)) {
                    cellFragment(ReferenceFragment::class)
                }
            }
        }
    }
}
