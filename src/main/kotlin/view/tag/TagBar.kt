package main.kotlin.view.tag

import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.layout.*
import main.kotlin.Styles
import main.kotlin.model.journal.Tag
import main.kotlin.model.journal.TagModel
import tornadofx.*

fun EventTarget.tagbar(values: ObservableValue<ObservableList<Tag>>, op: TagBar.() -> Unit = {}) =
    opcr(this, TagBar(values)) {
        this.fitToWidth(this)
        op.invoke(this)
    }

fun EventTarget.tagbar(values: ObservableList<Tag>? = null, op: TagBar.() -> Unit = {}) = opcr(this, TagBar(values)) {
    this.fitToWidth(this)
    op.invoke(this)
}

class TagBar(
    private val values: ObservableList<Tag>? = SimpleListProperty(mutableListOf<Tag>().asObservable()),
) : FlowPane() {
    constructor(values: ObservableValue<ObservableList<Tag>>) : this() {
        values.onChange { list -> if (list != null) this.values?.bind(list) { it } }
    }

    init {
        addClass(Styles.tags)
        values?.onChange { change ->
            children.clear()
            while (change.next()) {
                if (change.wasAdded()) children.addAll(
                    change.addedSubList
                        .map { TagBox(SimpleObjectProperty(it)) }
                        .sortedBy { it.entry.text.value.length })
            }
        }
    }

    private class TagBox(itemProperty: ObjectProperty<Tag>) : HBox() {
        val entry = TagModel(itemProperty)

        init {
            addClass(Styles.tag)
            background = Background(BackgroundFill(entry.colorValue.value, Styles.tagRadii, Insets.EMPTY))
            entry.colorValue.onChange { background = Background(BackgroundFill(it, Styles.tagRadii, Insets.EMPTY)) }

            hgrow = Priority.ALWAYS

            text(Bindings.concat("# ", entry.text)) {
                this.fill = entry.colorValue.value.invert()
                entry.colorValue.onChange { this.fill = it?.invert() }
            }
            tooltip { textProperty().bind(entry.description) }
        }
    }
}
