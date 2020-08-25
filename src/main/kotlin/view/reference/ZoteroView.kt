package main.kotlin.view.reference

import main.kotlin.controller.ReferencesController
import main.kotlin.controller.StoreController
import tornadofx.*

class ZoteroView : View() {
    val referencesController: ReferencesController by inject()
    val storeController: StoreController by inject()

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
                listview(observableListOf(storeController.authorMapping.values)) {
                    cellFragment(AuthorFragment::class)
                }
            }

            vbox {
                text("Items")
                listview(observableListOf(storeController.referenceMapping.values)) {
                    cellFragment(ReferenceFragment::class)
                }
            }
        }
    }
}
