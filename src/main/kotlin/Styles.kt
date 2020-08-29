package main.kotlin

import javafx.geometry.Orientation
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val title by cssid()

        val keywords by cssclass()
        val keywordTag by cssclass()
        val keywordRadii = CornerRadii(2.0)

        val entryItem by cssclass()

        val customContainer by cssclass()
        val nestedContainer by cssclass()
        val buttons by cssclass()

        val highlightColor = Color.YELLOW
        val hoverBackground = "black"
        val hoverTextColor = "white"
        val hoverPaddingPx = 5
    }

    init {
        title {
            startMargin = (10.px)
            fontSize = 2.em
        }

        customContainer {
            textField { backgroundRadius = multi(box(0.em)) }
            textArea { backgroundRadius = multi(box(0.em)) }
            padding = box(5.px)
            spacing = 5.px
            vgap = 10.px
            hgap = 10.px
        }
        nestedContainer {
            spacing = 5.px
            vgap = 10.px
            hgap = 10.px
        }

        buttons {
            spacing = 10.px
        }

        keywords {
            orientation = Orientation.HORIZONTAL
            maxHeight = 60.px
            backgroundColor += Color.TRANSPARENT
        }
        keywordTag {
            prefHeight = 40.px
            prefWidth = 170.px
        }

        entryItem {
            padding = box(0.px, 3.px)
            spacing = 3.px
        }
    }
}
