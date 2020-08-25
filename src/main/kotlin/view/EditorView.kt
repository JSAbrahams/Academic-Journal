package main.kotlin.view

import javafx.geometry.Orientation
import javafx.scene.control.Label
import javafx.stage.Popup
import main.kotlin.controller.EditorController
import main.kotlin.model.Keyword
import main.kotlin.view.fragment.KeywordFragment
import org.fxmisc.richtext.StyleClassedTextArea
import org.fxmisc.richtext.event.MouseOverTextEvent
import tornadofx.*
import java.time.Duration

class EditorView : View() {
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
            circle(radius = 3).visibleWhen(editorController.current.select { it.editedProperty })
            textfield(editorController.current.select { it.titleProperty }) {
                disableWhen(editorController.editMode.not().or(editorController.current.isNull))
            }
        }
        hbox {
            text("Creation")
            text(editorController.current.select { it.creationProperty.asString() })
            separator(Orientation.VERTICAL)
        }

        textarea(editorController.current.select { it.textProperty }) {
            disableWhen(editorController.editMode.not().or(editorController.current.isNull))
        }

        text("Keywords")
        hbox {
            button("+") {
                disableWhen(editorController.current.isNull.or(editorController.editMode.not()))
                action { editorController.current.get().keywordsProperty.add(Keyword()) }
            }
            listview(editorController.current.select { it.keywordsProperty }) {
                cellFragment(KeywordFragment::class)
            }
        }
    }
}
