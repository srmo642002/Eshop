package eshop

import org.apache.commons.collections.list.LazyList
import org.apache.commons.collections.FactoryUtils

class VariationGroup {
    static auditable = true
    String name
    String representationType
    ProductType productType
    Boolean showInFilter

    static hasMany = [variationValues: VariationValue, variations: Variation]
    static composites = ["variationValues"]
    List variationValues = LazyList.decorate(new ArrayList(), FactoryUtils.instantiateFactory(VariationValue.class))
    static mapping = {
        sort 'name'
        variationValues cascade: "all-delete-orphan"
    }

    static searchable = {
        root false
        only = ['name']
    }

    static constraints = {
        name()
        representationType(inList: ["String", "Color"])
        productType(nullable: true)
        showInFilter(nullable: true)
        variationValues()

    }
    static ignoredFieldsInJSON = ["variations"]

    String toString() {
        name
    }
}
