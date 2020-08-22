package main.kotlin

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val title by cssid()
        val container by cssclass()
    }

    init {
        title { fontSize = 2.em }

        container {
            textField { backgroundRadius = multi(box(0.em)) }
            textArea { backgroundRadius = multi(box(0.em)) }
        }
    }
}
