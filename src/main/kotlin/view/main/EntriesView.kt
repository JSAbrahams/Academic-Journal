package main.kotlin.view.main

import javafx.scene.control.ButtonType
import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.StoreController
import main.kotlin.view.fragment.EntryFragment
import tornadofx.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class EntriesView : View() {
    val editorController: EditorController by inject()
    val storeController: StoreController by inject()

    val menuViewView: MenuView by inject()

    override val root = vbox {
        addClass(Styles.customContainer)

        text("Entries") { setId(Styles.title) }

        listview(storeController.journal.select { it.itemsProperty }) {
            cellFragment(EntryFragment::class)
            bindSelected(editorController.current)

            storeController.journal.onChange { _ ->
                if (items.isNotEmpty()) {
                    scrollTo(items.size - 1)
                    selectionModel.select(items.size - 1)
                }
            }
        }

        hbox {
            addClass(Styles.buttons)
            togglebutton("Edit Mode") {
                disableWhen(storeController.journal.isNull)
                editorController.editMode.bind(this.selectedProperty())
            }
            button("save") {
                disableWhen(storeController.savedProperty)
                action { menuViewView.save() }
            }
            button("+").action {
                var selection = ButtonType.YES

                if (storeController.journal.isNotNull.get() && storeController.journal.get().itemsProperty.isNotEmpty()) {
                    val daysAfterEpoch = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), LocalDate.now())

                    val lastEntry = storeController.journal.get().itemsProperty.last()
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

                if (selection == ButtonType.YES) editorController.current.set(storeController.newEntry())
            }
        }
    }
}