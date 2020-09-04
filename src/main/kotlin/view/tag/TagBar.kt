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

fun tagbar(values: ObservableValue<ObservableList<Tag>>) = TagBar(values)
fun tagbar(values: ObservableList<Tag>? = null) = TagBar(values)

class TagBar(private val values: ObservableList<Tag>? = SimpleListProperty(mutableListOf<Tag>().asObservable())) :
    HBox() {
    constructor(values: ObservableValue<ObservableList<Tag>>) : this() {
        values.onChange { list -> if (list != null) this.values?.bind(list) { it } }
    }

    init {
        addClass(Styles.keywords)
        hgrow = Priority.ALWAYS
        background = Background.EMPTY

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
            addClass(Styles.keywordTag)
            tooltip { textProperty().bind(entry.description) }

            background = Background(BackgroundFill(entry.colorValue.value, Styles.keywordRadii, Insets.EMPTY))
            entry.colorValue.onChange { background = Background(BackgroundFill(it, Styles.keywordRadii, Insets.EMPTY)) }

            text("#")
            text(entry.text)
        }
    }
}
