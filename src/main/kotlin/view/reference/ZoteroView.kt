package main.kotlin.view.reference

import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.ReferencesController
import main.kotlin.model.ReferencePosition
import tornadofx.*

class ZoteroView : View() {
    val referencesController: ReferencesController by inject()
    val editorController: EditorController by inject()

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
                text("Authors") { addClass(Styles.title) }
                listview(observableListOf(referencesController.authorMapping.values)) {
                    cellFragment(AuthorFragment::class)
                }
            }

            vbox {
                text("Items") { addClass(Styles.title) }
                listview(observableListOf(referencesController.referenceMapping.values)) {
                    cellFragment(ReferenceFragment::class)

                    referencesController.selectedReference.onChange {
                        selectionModel.select(it)
                        scrollTo(selectionModel.selectedIndex)
                        focusModel.focus(selectionModel.selectedIndex)
                        requestFocus()
                    }
                }
            }
        }

        row {
            button("+") {
                disableWhen(
                    editorController.isValidSelection.not()
                        .or(editorController.isEditMode.not())
                        .or(referencesController.selectedReference.isNull)
                )
                action {
                    editorController.current.get().referencesProperty.add(
                        ReferencePosition(
                            start = editorController.selectionBounds.get().start,
                            end = editorController.selectionBounds.get().end,
                            reference = referencesController.selectedReference.get()
                        )
                    )
                    currentStage?.close()
                }
            }
        }
    }
}
