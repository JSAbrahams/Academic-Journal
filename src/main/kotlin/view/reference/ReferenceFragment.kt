package main.kotlin.view.reference

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import main.kotlin.Styles
import main.kotlin.controller.ReferencesController
import main.kotlin.model.reference.Reference
import main.kotlin.model.reference.ReferenceModel
import tornadofx.*

class ReferenceFragment(item: Property<Reference>? = null) : ListCellFragment<Reference>() {
    val referenceController: ReferencesController by inject()

    val entry = ReferenceModel(item?.let { SimpleObjectProperty(it.value) } ?: itemProperty)

    val showAbstract = SimpleBooleanProperty(false)

    override val root = gridpane {
        addClass(Styles.entryItem)

        run {
            val bindWhen = { ->
                val anyAuthorSelected = entry.authors.fold(
                    SimpleBooleanProperty(false),
                    fun(acc, b): BooleanBinding { return Bindings.or(acc, b.selectedProperty) })

                disableProperty().cleanBind(
                    if (entry.collection.isNull.get()) {
                        anyAuthorSelected
                    } else {
                        anyAuthorSelected.and(entry.collection.selectBoolean { it.selectedProperty })
                    }.not()
                )
            }

            bindWhen.invoke()
            entry.authors.onChange { bindWhen.invoke() }
        }

        onLeftClick {
            showAbstract.set(entry.abstract.isNotBlank().get() && !showAbstract.get())
            referenceController.selectedReferenceProperty.set(entry.item)
        }

        row {
            managedWhen(entry.title.isNotBlank())
            text(entry.title) {
                addClass(Styles.entryItemTitle)
                gridpaneConstraints { columnSpan = 2 }
            }
        }

        row {
            visibleWhen(entry.collection.isNotNull)
            managedWhen(entry.collection.isNotNull)

            text("Collection")
            text(entry.collection.select { it.nameProperty })
        }

        row {
            visibleWhen(entry.itemType.isNotBlank())
            managedWhen(entry.itemType.isNotBlank())

            text("Type")
            text(entry.itemType)
        }

        row {
            managedWhen(entry.authors.sizeProperty.greaterThan(0))
            visibleWhen(entry.authors.sizeProperty.greaterThan(0))

            text("Authors")
            listview(entry.authors) {
                cellFragment(SimpleAuthorFragment::class)
                prefHeightProperty().bind(Bindings.size(entry.authors).multiply(AuthorFragment.height))
            }
        }
    }
}
