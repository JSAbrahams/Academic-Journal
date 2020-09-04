package main.kotlin

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
        val textfield by cssclass()

        val tags by cssclass()
        val tag by cssclass()
        val tagRadii = CornerRadii(10.0)

        val entryItem by cssclass()

        val customContainer by cssclass()
        val nestedContainer by cssclass()
        val buttons by cssclass()

        val highlightColor = Color.YELLOW
        val hoverBackground = "black"
        val hoverTextColor = "white"
        val hoverPaddingPx = 5

        val webviewFontSizePx = 13
        val webViewFont = "Helvetica"
    }

    init {
        title {
            startMargin = (10.px)
            fontSize = 2.em
        }
        textfield {
            textBoxBorder = Color.TRANSPARENT
            focusColor = Color.TRANSPARENT
            faintFocusColor = Color.TRANSPARENT
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

        tags {
            hgap = 5.px
            vgap = 5.px
        }
        tag {
            padding = box(3.px)
            alignment = Pos.CENTER
            fontScale = 0.3
        }

        entryItem {
            padding = box(0.px, 3.px)
            spacing = 5.px
        }
    }
}
