package main.kotlin.view

import javafx.geometry.Orientation
import javafx.scene.control.Label
import javafx.stage.Popup
import main.kotlin.controller.EditorController
import org.fxmisc.richtext.StyleClassedTextArea
import org.fxmisc.richtext.event.MouseOverTextEvent
import tornadofx.View
import tornadofx.disableWhen
import tornadofx.hbox
import tornadofx.select
import tornadofx.separator
import tornadofx.text
import tornadofx.textarea
import tornadofx.textfield
import tornadofx.togglebutton
import tornadofx.vbox
import java.time.Duration

class Editor : View() {
    private val editorController: EditorController by inject()

    private val area: StyleClassedTextArea = StyleClassedTextArea()
    private val popup = Popup()
    private val popupMsg = Label()

    init {
        area.isWrapText = true
        popup.content.add(popupMsg)

        area.mouseOverTextDelay = Duration.ofSeconds(1)
        area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN) {
            val chIdx = it.characterIndex
            val pos = it.screenPosition
            popupMsg.text = "Character '" + area.getText(chIdx, chIdx + 1) + "' at " + pos
            popup.show(area, pos.x, pos.y + 10)
        }

        area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END) { popup.hide() }

        area.text(editorController.current.select { it.textProperty })
        area.disableProperty().bind(editorController.editMode.not())
    }

    override val root = vbox {
        hbox {
            textfield(editorController.current.select { it.titleProperty }).disableWhen(
                editorController.editMode.not()
            )
        }
        hbox {
            text("Creation")
            text(editorController.current.select { it.creationProperty.asString() })
            separator(Orientation.VERTICAL)
        }
        textarea(editorController.current.select { it.textProperty }).disableWhen(
            editorController.editMode.not()
        )
        togglebutton("Edit Mode") {
            editorController.editMode.bind(this.selectedProperty())
        }
    }
}
