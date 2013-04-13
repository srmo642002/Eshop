package eshop

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestContextHolder

import javax.servlet.http.Cookie

class ProductService {

    @Transactional()
    synchronized void addImageToProduct(productId, Set<Content> images) {
        synchronized (this) {
            def product = Product.lock(productId)
            product.images.addAll(images)
            product.merge(flush: true)
        }
    }

    @Transactional()
    synchronized boolean deleteProductImage(productId, imagename) {
        synchronized (this) {
            def product = Product.lock(productId)
            def image
            product.images.each {
                if (it.name == imagename) {
                    image = it
                }
            }
            if (image) {
                product.removeFromImages(image)
                product.merge(flush: true)
                return true
            }
            return false
        }
    }
    @Transactional()
    synchronized void addVideoToProduct(productId, Set<Content> images) {
        synchronized (this) {
            def product = Product.lock(productId)
            product.videos.addAll(images)
            product.merge(flush: true)
        }
    }

    @Transactional()
    synchronized boolean deleteProductVideo(productId, imagename) {
        synchronized (this) {
            def product = Product.lock(productId)
            def image
            product.videos.each {
                if (it.name == imagename) {
                    image = it
                }
            }
            if (image) {
                product.removeFromVideos(image)
                product.merge(flush: true)
                return true
            }
            return false
        }
    }

    def findRootProductTypes() {
        ProductType.findAllByParentProductIsNull()
    }

    def findLastVisitedProducts(lastVisitedProductsCookie){
        def session = RequestContextHolder.currentRequestAttributes().getSession()
        def lastVisitedProducts
        synchronized (this.getClass()) {
            lastVisitedProducts = session.getAttribute('lastVisitedProducts')
            if (!lastVisitedProducts) {
                lastVisitedProducts = []
                String lastVisitedProductsStr = lastVisitedProductsCookie
                if (lastVisitedProductsStr)
                    lastVisitedProducts = lastVisitedProductsStr.split(",").toList()
            }
        }

        if (lastVisitedProducts) {
            return Product.createCriteria().list() {
                'in'('id', lastVisitedProducts.collect() { it.toLong() })
            }
        }
    }
}
