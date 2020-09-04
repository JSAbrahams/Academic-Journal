package main.kotlin

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import tornadofx.*

val Color.web: String
    get() = {
        val r = Math.round(this.red * 255).toInt() shl 24
        val g = Math.round(this.green * 255).toInt() shl 16
        val b = Math.round(this.blue * 255).toInt() shl 8
        val a = Math.round(this.opacity * 255).toInt()
        String.format("#%08X", r + g + b + a)
    }.invoke()

class Styles : Stylesheet() {
    companion object {
        val title by cssclass()

        val keywords by cssclass()
        val keywordTag by cssclass()
        val keywordRadii = CornerRadii(5.0)

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
            backgroundInsets = multi(box(0.em))
            prefHeight = 20.px
        }
        keywordTag {
            maxHeight = 20.px
            prefWidth = 60.px
            padding = box(3.px)
            alignment = Pos.CENTER
        }

        entryItem {
            padding = box(0.px, 3.px)
            spacing = 3.px
        }
    }
}
