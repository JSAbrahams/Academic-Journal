package main.kotlin.view

import tornadofx.View
import tornadofx.borderpane

class MainView : View() {
    override val root = borderpane {
        top(MenuView::class)
        left(EntriesView::class)
        center(EditorView::class)
        right(ReferencesView::class)
    }
}
