package main.kotlin.view.reference.overview

import javafx.beans.binding.Bindings
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import main.kotlin.Styles
import main.kotlin.controller.OverviewController
import main.kotlin.model.journal.JournalEntryModel
import main.kotlin.model.journal.ReferencePosition
import tornadofx.*

class OverviewView : View() {
    private val overviewController: OverviewController by inject()

    override val root = gridpane {
        addClass(Styles.customContainer)

        columnConstraints.add(ColumnConstraints().also { it.hgrow = Priority.SOMETIMES })
        columnConstraints.add(ColumnConstraints().also { it.hgrow = Priority.ALWAYS })

        rowConstraints.add(RowConstraints().also { it.vgrow = Priority.NEVER })
        row {
            gridpane {
                gridpaneConstraints { columnSpan = 2 }
                addClass(Styles.nestedContainer)

                row {
                    text("Title")
                    text(overviewController.currentReference.select { it.titleProperty }) {
                        addClass(Styles.entryItemTitle)
                    }
                }
            }
        }

        rowConstraints.add(RowConstraints().also { it.vgrow = Priority.NEVER })
        row {
            text("Highlights")
            text("Summary")
        }

        rowConstraints.add(RowConstraints().also { it.vgrow = Priority.ALWAYS })
        row {
            listview(overviewController.highlightsProperty) {
                vgrow = Priority.ALWAYS
                hgrow = Priority.NEVER
                cellFragment(HighlightFragment::class)
            }

            tableview(overviewController.summaryProperty) {
                column("Entry", ReferencePosition::journalEntryProperty).cellFormat {
                    val model = JournalEntryModel(itemProperty())
                    graphic = vbox {
                        text(model.title) { addClass(Styles.entryItemTitle) }
                        text().bind(model.creation)
                        hbox {
                            text(rowItem.startProperty.asString())
                            text(":")
                            text(rowItem.endProperty.asString())
                        }
                    }
                }
                column("Summary", ReferencePosition::journalEntryProperty).cellFormat {
                    val model = JournalEntryModel(itemProperty())
                    graphic = scrollpane {
                        text(Bindings.createStringBinding({
                            model.text.value.substring(rowItem.startProperty.get(), rowItem.endProperty.get())
                        }))
                    }
                }
            }
        }
    }
}
