package main.kotlin.view.reference

import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.ReferencesController
import main.kotlin.model.journal.ReferencePosition
import main.kotlin.model.journal.ReferenceType
import main.kotlin.view.JournalView
import tornadofx.*

class ZoteroView : JournalView() {
    val referencesController: ReferencesController by inject()
    val editorController: EditorController by inject()

    override val root = gridpane {
        addClass(Styles.customContainer)

        columnConstraints.add(ColumnConstraints().also { it.hgrow = Priority.SOMETIMES })
        columnConstraints.add(ColumnConstraints().also { it.hgrow = Priority.SOMETIMES })
        columnConstraints.add(ColumnConstraints().also { it.hgrow = Priority.ALWAYS })

        rowConstraints.add(RowConstraints().also { it.vgrow = Priority.NEVER })
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

        rowConstraints.add(RowConstraints().also { it.vgrow = Priority.ALWAYS })
        row {
            vbox {
                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
                text("Authors") { addClass(Styles.title) }
                listview(observableListOf(referencesController.authorsProperty.values)) {
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                    cellFragment(AuthorFragment::class)
                }
            }

            vbox {
                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
                text("Collections") { addClass(Styles.title) }
                listview(observableListOf(referencesController.collectionsProperty.values)) {
                    vgrow = Priority.ALWAYS
                    cellFragment(CollectionFragment::class)
                }
            }

            vbox {
                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
                text("Items") { addClass(Styles.title) }
                listview(observableListOf(referencesController.filteredReferencesProperty)) {
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                    cellFragment(ReferenceFragment::class)

                    referencesController.selectedReferenceProperty.onChange {
                        selectionModel.select(it)
                        scrollTo(selectionModel.selectedIndex)
                        focusModel.focus(selectionModel.selectedIndex)
                        requestFocus()
                    }
                }
            }
        }

        rowConstraints.add(RowConstraints().also { it.vgrow = Priority.NEVER })
        row {
            button("Select all").action {
                referencesController.authorsProperty.forEach { it.value.selectedProperty.set(true) }
            }
            button("Select all").action {
                referencesController.collectionsProperty.forEach { it.value.selectedProperty.set(true) }
            }
        }

        rowConstraints.add(RowConstraints().also { it.vgrow = Priority.NEVER })
        row {
            hbox {
                addClass(Styles.buttons)

                button("+") {
                    enableWhen(
                        editorController.isValidSelection
                            .and(editorController.isEditable)
                            .and(referencesController.selectedReferenceProperty.isNotNull)
                    )
                    action {
                        editorController.current.get().referencesProperty.add(
                            ReferencePosition(
                                start = editorController.selectionBounds.get().start,
                                end = editorController.selectionBounds.get().end,
                                reference = referencesController.selectedReferenceProperty.get(),
                                referenceType = referencesController.selectedType.get()
                            )
                        )
                        currentStage?.close()
                    }
                }

                combobox(referencesController.selectedType, ReferenceType.values().asList())
            }
        }
    }
}
