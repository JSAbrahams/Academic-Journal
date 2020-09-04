package main.kotlin.view.main

import main.kotlin.view.JournalView
import tornadofx.borderpane

class MainView : JournalView() {
    override val root = borderpane {
        top(MenuView::class)
        left(EntriesView::class)
        center(EditorView::class)
        right(ReferencesView::class)
    }
}
