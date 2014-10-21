package eshop

import grails.converters.JSON

class AppController {

    def mostVisited() {
        def productType = ProductType.get(params.ptId)
        int offset = params.int('offset') ?: 0
        def products = Product.createCriteria().listDistinct {
            models {
                eq('status', 'exists')
            }
            or {
                isNull('isVisible')
                eq('isVisible', true)
            }
            if (productType) {
                productTypes {
                    'in'('id', productType?.allChildren?.collect { it.id } + [productType.id])
                }
            }
            maxResults(20)
            offset(offset)
            order("visitCount", "desc")
        }.collect {
            [id: it.id, title: "${product?.manualTitle ? product?.pageTitle : "${product?.productTypes?.find { true }?.name ?: ""} ${product?.type?.title ?: ""} ${product?.brand?.name ?: ""} ${product?.name ?: ""}"}"]
        }
        render products as JSON
    }
}
