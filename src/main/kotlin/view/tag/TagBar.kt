package main.kotlin.view.tag

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import main.kotlin.Styles
import main.kotlin.model.Tag
import main.kotlin.model.TagModel
import tornadofx.*

fun tagbar(values: ObservableValue<ObservableList<Tag>>, op: TagBar.() -> Unit = {}) = TagBar(values, op)
fun tagbar(values: ObservableList<Tag>? = null, op: TagBar.() -> Unit = {}) = TagBar(values, op)

class TagBar(
    private val values: ObservableList<Tag>? = SimpleListProperty(mutableListOf<Tag>().asObservable()),
    op: TagBar.() -> Unit = {}
) : HBox() {
    constructor(values: ObservableValue<ObservableList<Tag>>, op: TagBar.() -> Unit) : this(op = op) {
        values.onChange { list -> if (list != null) this.values?.bind(list) { it } }
    }

    init {
        addClass(Styles.tags)
        op.invoke(this)

        values?.onChange { change ->
            while (change.next()) {
                if (change.wasAdded()) {
                    change.addedSubList.forEach { tag -> this.children.add(TagFragment(SimpleObjectProperty(tag))) }
                } else {
                    change.removed.forEach { tag ->
                        this.children.removeAll(
                            this.children.filter { it is TagFragment && it.itemProperty.isNotNull.get() && it.itemProperty.value == tag }
                        )
                    }
                }
            }
        }
    }

    private class TagFragment(val itemProperty: ObjectProperty<Tag>) : HBox() {
        val entry = TagModel(itemProperty)

        init {
            addClass(Styles.tag)
            background = Background(BackgroundFill(entry.colorValue.value, Styles.tagRadii, Insets.EMPTY))
            entry.colorValue.onChange { background = Background(BackgroundFill(it, Styles.tagRadii, Insets.EMPTY)) }

            hgrow = Priority.ALWAYS

            text("#")
            text(entry.text)
            tooltip { textProperty().bind(entry.description) }
        }
    }
}
