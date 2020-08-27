package main.kotlin.view.reference

import main.kotlin.Styles
import main.kotlin.controller.ReferencesController
import tornadofx.*

class ZoteroView : View() {
    val referencesController: ReferencesController by inject()

    override val root = gridpane {
        addClass(Styles.customContainer)

        row {
            gridpane {
                gridpaneConstraints { columnSpan = 2 }
                addClass(Styles.nestedContainer)

                row {
                    text("Data ")
                    text(referencesController.location.asString())
                }
                row {
                    button("Resync").action {
                        referencesController.refreshReferences()
                    }
                    hbox {
                        text("Last Sync ")
                        text(referencesController.lastSync.asString())
                    }
                }
            }

        }


        row {
            vbox {
                text("Authors") { setId(Styles.title) }
                listview(observableListOf(referencesController.authorMapping.values)) {
                    cellFragment(AuthorFragment::class)
                }
            }

            vbox {
                text("Items") { setId(Styles.title) }
                listview(observableListOf(referencesController.referenceMapping.values)) {
                    cellFragment(ReferenceFragment::class)
                }
            }
        }
    }
}
