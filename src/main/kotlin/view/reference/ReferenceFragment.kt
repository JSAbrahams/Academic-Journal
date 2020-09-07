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
                    fun(acc, b): BooleanBinding { return Bindings.or(acc, b.selectedProperty.also { println(it) }) })

                disableProperty().cleanBind(
                    Bindings.`when`(entry.subCollection.isNotNull)
                        .then(anyAuthorSelected.and(
                            // Final check, else NullPointer if null on view initialization (selectBoolean cannot handle null)
                            if (entry.subCollection.isNotNull.get())
                                entry.subCollection.selectBoolean { it.selectedProperty }
                            else SimpleBooleanProperty(true)))
                        .otherwise(anyAuthorSelected)
                        .not())
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
                cellFragment(AuthorFragment::class)
                prefHeightProperty().bind(Bindings.size(entry.authors).multiply(AuthorFragment.height))
            }
        }
    }
}
