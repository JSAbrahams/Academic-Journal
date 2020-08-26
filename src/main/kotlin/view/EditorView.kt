package main.kotlin.view

import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.stage.Popup
import main.kotlin.Styles
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
        addClass(Styles.customContainer)

        hbox {
            region {
                vgrow = Priority.ALWAYS
                val height = heightProperty()
                circle(radius = 5) {
                    centerYProperty().bind(height.divide(2.0))
                    visibleWhen(editorController.current.select { it.editedProperty })
                    managedWhen(editorController.current.select { it.editedProperty })
                }
            }
            text(editorController.current.select { it.creationProperty.asString() }) { setId(Styles.title) }
        }

        textfield(editorController.current.select { it.titleProperty }) {
            promptText = "Title"
            disableWhen(editorController.editMode.not().or(editorController.current.isNull))
        }
        textarea(editorController.current.select { it.textProperty }) {
            promptText = "Today I..."
            disableWhen(editorController.editMode.not().or(editorController.current.isNull)))
        }

        hbox {
            addClass(Styles.buttons)
            text("Last Edit")
            text(editorController.current.select { it.lastEditProperty.asString() })
        }

        text("Keywords")
        hbox {
            addClass(Styles.buttons)
            button("+") {
                disableWhen(editorController.current.isNull.or(editorController.editMode.not()))
                action { editorController.current.get().keywordsProperty.add(Keyword()) }
            }
            listview(editorController.current.select { it.keywordsProperty }) {
                addClass(Styles.keywords)
                hgrow = Priority.ALWAYS
                cellFragment(KeywordFragment::class)
            }
        }
    }
}
