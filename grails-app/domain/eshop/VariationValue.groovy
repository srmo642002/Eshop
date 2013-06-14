package eshop

class VariationValue {
    static auditable = true
    String value
    VariationGroup variationGroup

    //static hasMany = [variations: Variation, productInstances: ProductInstance]

    static belongsTo = [VariationGroup, Variation, ProductInstance]
    Boolean deleted
    Integer indx

    static searchable = {
//        root false
//        only = ['value']
    }

    static transients = ['deleted']

    static mapping = {
        sort 'value'
    }

    static constraints = {
        value(unique: 'variationGroup')
    }

    String toString() {
        value
    }
}
