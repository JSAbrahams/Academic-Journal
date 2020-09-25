package main.kotlin.view.reference.overview

import javafx.beans.binding.Bindings
import javafx.scene.input.MouseButton
import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.model.journal.ReferencePosition
import main.kotlin.model.journal.ReferencePositionModel
import tornadofx.*
import java.lang.Integer.max
import java.lang.Integer.min

class HighlightFragment : ListCellFragment<ReferencePosition>() {
    private val range = 100

    private val editorController: EditorController by inject()

    private val entry = ReferencePositionModel(itemProperty)

    override val root = vbox {
        text(entry.journalEntry.select { it.titleProperty }) { addClass(Styles.entryItemTitle) }

        hbox {
            text("-")
            text(entry.start)
            text(":")
            text(entry.end)
        }

        text(Bindings.createStringBinding({
            entry.journalEntry.select { it.textProperty }.value?.substring(
                max(itemProperty.value.startProperty.get() - range, 0),
                itemProperty.value.startProperty.get()
            )?.trimStart()
        }, entry.journalEntry.select { it.textProperty })) {
            managedWhen(textProperty().isNotEmpty)
            addClass(Styles.greyed)
        }

        text(Bindings.createStringBinding({
            entry.journalEntry.select { it.textProperty }.value?.substring(
                itemProperty.value.startProperty.get(),
                itemProperty.value.endProperty.get()
            )
        }, entry.journalEntry.select { it.textProperty }))

        text(Bindings.createStringBinding({
            val string: String? = entry.journalEntry.select { it.textProperty }.value
            string?.substring(
                itemProperty.value.endProperty.get(),
                min(itemProperty.value.endProperty.get() + range, string.length)
            )?.trimEnd()
        }, entry.journalEntry.select { it.textProperty })) {
            managedWhen(textProperty().isNotEmpty)
            addClass(Styles.greyed)
        }


        setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY && it.clickCount == 2) {
                editorController.current.set(entry.journalEntry.value)
            }
        }
    }
}
