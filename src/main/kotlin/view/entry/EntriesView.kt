package main.kotlin.view.entry

import javafx.scene.control.ButtonType
import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.JournalController
import main.kotlin.view.JournalView
import main.kotlin.view.fragment.EntryFragment
import tornadofx.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class EntriesView : JournalView() {
    val editorController: EditorController by inject()
    val journalController: JournalController by inject()

    override val root = vbox {
        addClass(Styles.customContainer)

        text("Entries") { addClass(Styles.title) }

        listview(journalController.journalProperty.select { it.entriesProperty }) {
            cellFragment(EntryFragment::class)
            vgrow = Priority.ALWAYS
            hgrow = Priority.NEVER
            bindSelected(editorController.current)

            val selectLastItem = {
                if (items.isNotEmpty()) {
                    selectionModel.select(items.size - 1)
                    scrollTo(selectionModel.selectedIndex)
                    focusModel.focus(selectionModel.selectedIndex)
                }
            }

            journalController.journalProperty.onChange { selectLastItem.invoke() }
            // If journal set before attaching listener, still select last item
            if (journalController.journalProperty.isNotNull.get()) selectLastItem.invoke()

        }

        hbox {
            addClass(Styles.buttons)
            togglebutton("Edit Mode") {
                disableWhen(journalController.journalProperty.isNull)
                editorController.isEditMode.bindBidirectional(this.selectedProperty())
            }
            button("save") {
                disableWhen(journalController.journalProperty.select { it.editedProperty.not() })
                action { save() }
            }
            button("+").action {
                var selection = ButtonType.YES

                if (journalController.journalProperty.isNotNull.get() && journalController.journalProperty.get().entriesProperty.isNotEmpty()) {
                    val daysAfterEpoch = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), LocalDate.now())

                    val lastEntry = journalController.journalProperty.get().entriesProperty.last()
                    val lastDate = lastEntry.creationProperty.get()
                    val journalDaysAfterEpoch = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), lastDate)

                    if (journalDaysAfterEpoch == daysAfterEpoch)
                        warning(
                            header = "There is already an entry for today. Do you still wish to creat another?",
                            buttons = arrayOf<ButtonType>(ButtonType.YES, ButtonType.NO),
                            owner = this.scene.window,
                            actionFn = { buttonType -> selection = buttonType }
                        )
                }

                if (selection == ButtonType.YES) editorController.current.set(journalController.newEntry())
            }
        }
    }
}