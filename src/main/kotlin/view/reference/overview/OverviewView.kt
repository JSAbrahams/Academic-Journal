package main.kotlin.view.reference.overview

import javafx.beans.binding.Bindings
import javafx.scene.control.TableRow
import javafx.scene.input.MouseButton
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.OverviewController
import main.kotlin.model.journal.JournalEntryModel
import main.kotlin.model.journal.ReferencePosition
import tornadofx.*


class OverviewView : View() {
    private val overviewController: OverviewController by inject()
    private val editorController: EditorController by inject()

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
            text("Highlights") { addClass(Styles.title) }
            text("Summary") { addClass(Styles.title) }
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
                    graphic = text(Bindings.createStringBinding({
                        model.text.value.substring(rowItem.startProperty.get(), rowItem.endProperty.get())
                    })) {
                        isWrapText = true
                    }
                }

                setRowFactory {
                    val row = TableRow<ReferencePosition>()
                    row.setOnMouseClicked {
                        if (!row.isEmpty && it.button == MouseButton.PRIMARY && it.clickCount == 2) {
                            editorController.current.set(row.item?.journalEntryProperty?.value)
                        }
                    }
                    row
                }
            }
        }
    }
}
