package main.kotlin.view.main

import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.stage.Popup
import main.kotlin.JournalApp
import main.kotlin.Styles
import main.kotlin.controller.EditorController
import main.kotlin.controller.JournalController
import main.kotlin.controller.ReferencesController
import main.kotlin.model.journal.JournalEntry
import main.kotlin.model.journal.ReferencePosition
import main.kotlin.view.JournalView
import main.kotlin.view.tag.tagbar
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.fxmisc.richtext.InlineCssTextArea
import org.fxmisc.richtext.SelectionImpl
import org.fxmisc.richtext.event.MouseOverTextEvent
import tornadofx.*
import java.time.Duration

class EditorView : JournalView() {
    private val editorController: EditorController by inject()
    private val journalController: JournalController by inject()
    private val referencesController: ReferencesController by inject()

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

        vbox {
            vgrow = Priority.ALWAYS

            textfield(editorController.current.select { it.titleProperty }) {
                promptText = "Title"
                addClass(Styles.title)
                addClass(Styles.textfield)

                disableWhen(editorController.isEditable.not())
                visibleWhen(editorController.isEditable)
                managedWhen(editorController.isEditable)
            }

            webview {
                val parser = Parser.builder().build()
                // Retrieved from https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-svg.js
                val script = JournalApp::class.java.getResource("/javascript/mathjax.js").readText()
                val config = JournalApp::class.java.getResource("/javascript/config.js").readText()

                fun update() {
                    if (editorController.isEditable.not().get() && editorController.current.isNotNull.get()) {
                        val text = editorController.current.value.asSimpleMarkdown()
                        val document = parser.parse(text)
                        val html = "<script>$config</script>" +
                                "<script type=\"text/javascript\" id=\"MathJax-script\" async>$script</script>" +
                                HtmlRenderer.builder().build().render(document)
                        engine.loadContent(html)
                    }
                }

                editorController.isEditable.onChange { update() }
                editorController.current.onChange { update() }

                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS

                disableWhen(editorController.isEditable)
                visibleWhen(editorController.isEditable.not())
                managedWhen(editorController.isEditable.not())
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

                // binding does not work, so manually update each time instead
                val updateTextArea = { oldEntry: JournalEntry?, newEntry: JournalEntry? ->
                    // Unbind so changes to area do not affect old value
                    oldEntry?.textProperty?.unbind()
                    oldEntry?.referencesProperty?.forEach {
                        it.startProperty.unbind()
                        it.endProperty.unbind()
                    }

                    area.clear()
                    if (newEntry != null) area.insertText(0, newEntry.textProperty.value)
                    newEntry?.textProperty?.bind(area.textProperty())

                    val setStyle: (ReferencePosition) -> Unit = { referencePosition ->
                        val selectionImpl = SelectionImpl("${referencePosition.hashCode()}", area) { path ->
                            path.strokeWidth = 0.0
                            path.fill = referencesController.typeColors[referencePosition.typeProperty.value]
                        }

                        if (area.text.length >= referencePosition.endProperty.get()) {
                            area.addSelection(selectionImpl)
                            selectionImpl.selectRange(
                                referencePosition.startProperty.get(),
                                referencePosition.endProperty.get()
                            )

                            referencePosition.startProperty.bind(selectionImpl.startPositionProperty())
                            referencePosition.endProperty.bind(selectionImpl.endPositionProperty())
                        }
                    }

                    newEntry?.referencesProperty?.forEach(setStyle)
                    newEntry?.referencesProperty?.onChange<ObservableList<ReferencePosition>> {
                        (0 until area.paragraphs.size).forEach { paragraph -> area.clearStyle(paragraph) }
                        it?.forEach(setStyle)
                    }
                }

                editorController.current.addListener { _, oldEntry, newEntry -> updateTextArea(oldEntry, newEntry) }
                // Add in case journal loaded before listener attached
                if (editorController.current.isNotNull.get()) updateTextArea(null, editorController.current.value)

                editorController.selectionBounds.bind(area.selectionProperty())
                area.hgrow = Priority.ALWAYS
                area.vgrow = Priority.ALWAYS
                area.isWrapText = true

                editorController.rowPosition.bind(area.currentParagraphProperty())
                editorController.colPosition.bind(area.caretColumnProperty())

                // does not work nicely with TornadoFX, so add manually
                popup.content.add(popupMsg)
                children.add(area)

                area.disableWhen(editorController.isEditable.not())
                area.visibleWhen(editorController.isEditable)
                area.managedWhen(editorController.isEditable)
            }
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
                itemsProperty().bind(journalController.journalProperty.select { it.keywordList })
                editorController.selectedKeywordProperty.bindBidirectional(valueProperty())
            }
        }

        tagbar(editorController.current.select { it.tagList })
    }
}
