package eshop

class ProductTypeType {
    static auditable = true
    String title
    byte[] image

    static belongsTo = [ProductType,Product]
    static constraints = {
        title()
        image(maxSize: 10000000,nullable: true)
    }
}
