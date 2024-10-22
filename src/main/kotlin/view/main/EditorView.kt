package main.kotlin.view.main

import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.Priority
import javafx.stage.Popup
import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.JournalController
import main.kotlin.model.ReferencePosition
import main.kotlin.view.keyword.KeywordFragment
import org.fxmisc.richtext.InlineCssTextArea
import org.fxmisc.richtext.SelectionImpl
import org.fxmisc.richtext.event.MouseOverTextEvent
import tornadofx.*
import java.time.Duration

class EditorView : View() {
    private val editorController: EditorController by inject()
    private val journalController: JournalController by inject()

    private val hoverDurationMillis = 200L

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
            text {
                addClass(Styles.title)
                bind(editorController.current.select { it.creationProperty })
            }
        }

        textfield(editorController.current.select { it.titleProperty }) {
            promptText = "Title"
            disableWhen(editorController.isEditable.not())
        }

        run {
            val area = InlineCssTextArea()
            val popup = Popup()
            val popupMsg = Label()
            popupMsg.style = "-fx-background-color: ${Styles.hoverBackground}; " +
                    "-fx-text-fill: ${Styles.hoverTextColor}; " +
                    "-fx-padding: ${Styles.hoverPaddingPx};"

            // Show reference(s) on hover in tooltip
            area.mouseOverTextDelay = Duration.ofMillis(hoverDurationMillis)
            area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN) {
                if (editorController.current.isNotNull.get()) {
                    val chIdx = it.characterIndex
                    val pos = it.screenPosition
                    val items = mutableListOf<Pair<Pair<Int, Int>, String>>()
                    val referencePos = mutableListOf<ReferencePosition>()

                    for (referencePosition in editorController.current.get().referencesProperty) {
                        if (referencePosition.startProperty <= chIdx && referencePosition.endProperty >= chIdx
                            && referencePosition.referenceProperty.isNotNull.get()
                        ) {
                            items += Pair(
                                Pair(referencePosition.startProperty.get(), referencePosition.endProperty.get()),
                                referencePosition.referenceProperty.get().title
                            )
                            referencePos.add(referencePosition)
                        }
                    }

                    if (items.isEmpty()) return@addEventHandler

                    editorController.hoveredReferencePosition.addAll(referencePos)
                    if (items.size > 1) {
                        val stringBuilder = StringBuilder()
                        items.forEach { i -> stringBuilder.append("${i.first.first} - ${i.first.second}: ${i.second}\n") }
                        popupMsg.text = stringBuilder.toString()
                    } else {
                        popupMsg.text = "${items[0].first.first} - ${items[0].first.second}: ${items[0].second}\n"
                    }
                    popup.show(area, pos.x, pos.y + 10)
                }
            }
            area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END) { popup.hide() }

            val selections = mutableListOf<SelectionImpl<String, String, String>>()
            // Used to unbind positions to highlights before clearing text
            val referencePositions = mutableSetOf<ReferencePosition>()

            // binding does not work, so manually update each time instead
            editorController.current.addListener { _, oldValue, newValue ->
                // After clearing make sure old value still has the proper text value
                val oldText = area.text
                referencePositions.forEach {
                    it.startProperty.unbind()
                    it.endProperty.unbind()
                }

                area.clear()
                oldValue?.textProperty?.set(oldText)

                if (newValue != null) area.insertText(0, newValue.textProperty.value)
            }
            area.textProperty().onChange {
                if (editorController.current.isNotNull.get()) editorController.current.value.textProperty.set(it)
            }

            editorController.selectionBounds.bind(area.selectionProperty())
            // highlight references and set text manually
            editorController.current.onChange { journalEntry ->
                val setStyle: (ReferencePosition) -> Unit = { referencePosition ->
                    val selectionImpl = SelectionImpl("${referencePosition.hashCode()}", area) { path ->
                        path.strokeWidth = 0.0
                        path.fill = Styles.highlightColor
                    }

                    if (area.text.length >= referencePosition.endProperty.get()) {
                        selections.add(selectionImpl)
                        area.addSelection(selectionImpl)

                        selectionImpl.selectRange(
                            referencePosition.startProperty.get(),
                            referencePosition.endProperty.get()
                        )

                        referencePosition.startProperty.bind(selectionImpl.startPositionProperty())
                        referencePosition.endProperty.bind(selectionImpl.endPositionProperty())
                    }
                }

                referencePositions.addAll(journalEntry?.referencesProperty ?: emptySet())
                journalEntry?.referencesProperty?.forEach(setStyle)
                journalEntry?.referencesProperty?.onChange<ObservableList<ReferencePosition>> {
                    (0 until area.paragraphs.size).forEach { paragraph -> area.clearStyle(paragraph) }
                    it?.forEach(setStyle)

                    referencePositions.clear()
                    referencePositions.addAll(it?.toSet() ?: emptySet())
                }
            }
            area.textProperty().onChange { text ->
                if (editorController.current.isNotNull.get() && text != null) {
                    editorController.current.value.referencesProperty.removeIf { it.startProperty.get() >= text.length }
                }
            }

            area.hgrow = Priority.ALWAYS
            area.vgrow = Priority.ALWAYS
            area.isWrapText = true

            editorController.rowPosition.bind(area.currentParagraphProperty())
            editorController.colPosition.bind(area.caretColumnProperty())

            // does not work nicely with TornadoFX, so add manually
            popup.content.add(popupMsg)
            children.add(area)
            area.disableWhen(editorController.isEditable.not())
        }

        hbox {
            addClass(Styles.buttons)
            text("Last Edit")
            text(editorController.current.select { it.lastEditProperty.asString() })

            hbox {
                alignment = Pos.CENTER_RIGHT

                text(editorController.rowPosition.asString())
                text(":")
                text(editorController.colPosition.asString())
            }
        }

        hbox {
            text("Tags")
            addClass(Styles.buttons)
            button("+") {
                disableWhen(editorController.selectedKeywordProperty.isNull.or(editorController.isEditable.not()))
                action {
                    if (editorController.selectedKeywordProperty.isNotNull.get()) {
                        val selected = editorController.selectedKeywordProperty.value ?: null
                        editorController.current.get().tagsProperty.add(selected)
                    }
                }
            }
            combobox(editorController.selectedKeywordProperty) {
                itemsProperty().bind(journalController.journal.select { it.keywordList })
                editorController.selectedKeywordProperty.bindBidirectional(valueProperty())
            }
        }

        listview(editorController.current.select { it.tagList }) {
            addClass(Styles.keywords)
            hgrow = Priority.ALWAYS
            background = Background.EMPTY

            cellFragment(KeywordFragment::class)
        }
    }
}
