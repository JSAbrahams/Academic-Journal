package main.kotlin.view.reference

import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.Priority
import main.kotlin.controller.ReferencesController
import main.kotlin.model.reference.Reference
import main.kotlin.model.reference.ReferenceModel
import tornadofx.*

class ReferenceFragment(item: Property<Reference>? = null) : ListCellFragment<Reference>() {
    val referenceController: ReferencesController by inject()

    val entry = ReferenceModel(item?.let { SimpleObjectProperty(it.value) } ?: itemProperty)

    val showAbstract = SimpleBooleanProperty(false)

    // TODO move to stylesheet
    private val WIDTH = 200.0

    override val root = vbox {
        hgrow = Priority.ALWAYS
        maxWidth = WIDTH

        onLeftClick {
            showAbstract.set(entry.abstract.isNotBlank().get() && !showAbstract.get())
            referenceController.selectedReference.set(entry.item)
        }

        text(entry.title).managedWhen(entry.title.isNotBlank())
        hbox {
            visibleWhen(entry.itemType.isNotBlank())
            managedWhen(entry.itemType.isNotBlank())
            text("Type: ")
            text(entry.itemType)
        }

        text("Authors")
        listview(entry.authors) {
            managedWhen(entry.authors.sizeProperty.greaterThan(0))
            cellFragment(AuthorFragment::class)
            prefHeightProperty().bind(Bindings.size(entry.authors).multiply(AuthorFragment.height))
        }

        vbox {
            visibleWhen(showAbstract)
            managedWhen(showAbstract)

            text("Abstract:")
            text(entry.abstract) {
                wrappingWidth = WIDTH
            }
        }
    }
}
