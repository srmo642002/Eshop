package eshop

class Price {
    Product product
    Double price = 0D
    Double rialPrice = 0D
    Currency currency
    Date startDate
    Date endDate



    static constraints = {
        price(min: 0D)
        rialPrice()
        currency(nullable: true)
        startDate(nullable: false)
        endDate(nullable: true)
    }
}