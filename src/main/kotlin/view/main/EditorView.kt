package main.kotlin.view.main

import javafx.scene.layout.Priority
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

        run {
            val area = StyleClassedTextArea()
            val popup = javafx.stage.Popup()
            val popupMsg = javafx.scene.control.Label()

            area.mouseOverTextDelay = Duration.ofMillis(100)
            area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN) {
                val chIdx = it.characterIndex
                val pos = it.screenPosition
                popupMsg.text = "Character '" + area.getText(chIdx, chIdx + 1) + "' at " + pos
                popup.show(area, pos.x, pos.y + 10)
            }
            editorController.caretPosition.bind(area.caretPositionProperty())

            area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END) { popup.hide() }
            // binding does not work, so manually update each time instead
            area.textProperty().onChange {
                if (editorController.current.isNotNull.get()) editorController.current.value.textProperty.set(it)
            }
            area.disableWhen(editorController.editMode.not())

            area.hgrow = Priority.ALWAYS
            area.vgrow = Priority.ALWAYS
            area.isWrapText = true
            popup.content.add(popupMsg)
            // does not work nicely with TornadoFX, so add manually
            children.add(area)
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
