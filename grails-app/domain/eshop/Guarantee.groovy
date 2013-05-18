package eshop

class Guarantee {
    static auditable = true

    String name
    String description
    String period
    byte[] logo

    static hasMany = [productTypeBrands: ProductTypeBrand]
    static composites = ["productTypeBrands"]

    static searchable = true

    static mapping = {
        sort 'name'
    }

    static constraints = {
        name(nullable: true)
        period(nullable: true)
        description(nullable: true)
        logo(nullable: true,maxSize: 1000000000)

    }

    String toString(){
        name
    }

}
