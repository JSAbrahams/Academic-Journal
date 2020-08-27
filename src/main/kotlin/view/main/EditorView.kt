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
                if (editorController.current.isNotNull.get()) {
                    val chIdx = it.characterIndex
                    for (referencePosition in editorController.current.get().referencesProperty) {
                        if (referencePosition.startProperty <= chIdx && referencePosition.endProperty >= chIdx
                            && referencePosition.referenceProperty.isNotNull.get()
                        ) {
                            popupMsg.text = referencePosition.referenceProperty.get().title

                            val pos = it.screenPosition
                            popup.show(area, pos.x, pos.y + 10)
                            break
                        }
                    }
                }
            }
            editorController.selectionBounds.bind(area.selectionProperty())

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
