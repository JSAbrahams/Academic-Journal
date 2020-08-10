package main.kotlin

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val title by cssid()
        val container by cssclass()
    }

    init {
        container {
            padding = box(100.px)
            title { fontSize = 10.em }
            textField { backgroundRadius = multi(box(0.em)) }
            textArea { backgroundRadius = multi(box(0.em)) }
        }
    }
}
