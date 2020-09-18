package main.kotlin.view.reference

import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import main.kotlin.Styles
import main.kotlin.controller.OverviewController
import main.kotlin.controller.ReferencesController
import main.kotlin.model.reference.Reference
import main.kotlin.model.reference.ReferenceModel
import main.kotlin.view.reference.overview.OverviewView
import tornadofx.*

class ReferenceFragment(item: Property<Reference>? = null) : ListCellFragment<Reference>() {
    val entry = ReferenceModel(item?.let { SimpleObjectProperty(it.value) } ?: itemProperty)

    private val referenceController: ReferencesController by inject()
    private val overviewController: OverviewController by inject()

    private val overviewView: OverviewView by inject()

    override val root = gridpane {
        addClass(Styles.entryItem)

        onLeftClick {
            referenceController.selectedReferenceProperty.set(entry.item)
        }

        row {
            button("Overview") {
                action {
                    overviewController.currentReference.set(itemProperty.value)
                    overviewView.openWindow(owner = currentStage, block = true)
                }
            }

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
            visibleWhen(entry.doi.isNotBlank())
            managedWhen(entry.doi.isNotBlank())

            text("DOI")
            text(entry.doi)
        }

        row {
            visibleWhen(entry.url.isNotBlank())
            managedWhen(entry.url.isNotBlank())

            text("URL")
            hyperlink(entry.url).action { hostServices.showDocument(entry.url.value) }
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
