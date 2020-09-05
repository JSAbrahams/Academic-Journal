package main.kotlin.view.main

import main.kotlin.view.JournalView
import main.kotlin.view.entry.EntriesView
import main.kotlin.view.reference.ReferencesView
import tornadofx.borderpane

class MainView : JournalView() {
    override val root = borderpane {
        top(MenuView::class)
        left(EntriesView::class)
        center(EditorView::class)
        right(ReferencesView::class)
    }
}
