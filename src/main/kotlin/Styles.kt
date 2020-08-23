package main.kotlin

import javafx.geometry.Orientation
import javafx.scene.paint.Color
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val title by cssid()

        val keywords by cssclass()
        val keywordsSmall by cssclass()

        val container by cssclass()
    }

    init {
        title { fontSize = 2.em }

        container {
            textField { backgroundRadius = multi(box(0.em)) }
            textArea { backgroundRadius = multi(box(0.em)) }

            box {
                spacing = 5.px
            }
        }

        keywords {
            orientation = Orientation.HORIZONTAL
            backgroundColor += Color.TRANSPARENT
        }
        keywordsSmall {
            prefHeight = 40.px
            prefWidth = 170.px
        }
    }
}
