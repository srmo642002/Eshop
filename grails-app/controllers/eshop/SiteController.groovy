package eshop

import grails.converters.JSON
import groovy.sql.Sql
import org.apache.lucene.search.BooleanQuery
import search.FarsiNormalizationFilter

import javax.servlet.http.Cookie

class SiteController {

    static defaultAction = "index"

    def browseService
    def priceService
    def searchableService
    def dataSource
    def springSecurityService
    def trackingService
    def messageService
    def mongoService
    def mailService
    def simpleCaptchaService
    def imageService
    def grailsApplication

    def findProducts() {

    }


    def findFilters() {

    }

    def suggestFriend() {
        def product = Product.get(params.id)
        def name = params.name
        def email = params.email
        if (product && name) {
            mailService.sendMail {
                to email
                subject(message(code: 'emailTemplates.suggest.subject', args: [name, message(code: 'name')]))
                html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                        model: [message: g.render(template: '/messageTemplates/mail/suggest_friend', model: [product: product, name: name]).toString()])
            }
        }
        render 0
    }

    def browse() {
        def productType = params.productType ? ProductType.findBySeoFriendlyNameAndDeleted(params.productType, false) ?: ProductType.findBySeoFriendlyAlternativeName(params.productType) ?: ProductType.findByName(params.productType) : null
        if (!productType || productType.deleted) {
            redirect(uri: "/notFound")
//            render ''
            return
        }

        trackingService.trackExplore(productType)

        def model = [productType: productType]

        model.commonLink = createLink(uri: '/browse')

        def productTypeChain = []
        def productTypeNavigator = productType
        while (productTypeNavigator) {
            productTypeChain << productTypeNavigator
            productTypeNavigator = productTypeNavigator.parentProduct
        }
        productTypeChain = productTypeChain.reverse()

        model.breadCrumb = []

        productTypeChain.each {
            model.breadCrumb << [name: it.name, href: "${model.commonLink}/${it.urlName}/"]
        }

        model.subProductTypeLinks = []
        def base = "${model.commonLink}/"

        productType.children.findAll { !it.deleted }.each {
            model.subProductTypeLinks << [name: it.name, href: base + it.urlName, id: it.id]
        }

        ProductType.createCriteria().listDistinct {
            ne('deleted', true)
            godFathers {
                eq('id', productType.id)
                ne('deleted', true)
            }
        }.each {
            model.subProductTypeLinks << [name: it.name, href: base + it.urlName, id: it.id]
        }

        model.articles = JournalArticle.findAllByBaseProduct productType

//        model.productTypeTypeLinks = []
//        if (productType.children.isEmpty()) {
//            productType.types.each {
//                model.productTypeTypeLinks << [name: it.title, href: createLink(action: "filter", params: [f: "p${productType.id},t${it.id}"]), id: it.id]
//            }
//        }

        model.rootProductTypes = ProductType.findAllByParentProductIsNull()
        model.filters = browseService.findProductTypeFilters(model.productType, params.page ?: 0, "${model.productType ? model.productType.id : ''} ${params.page ?: 0}")

        if (model.filters.products.productIds.size() == 0) {
            redirect(uri: "/notFound")
            return
        }

        model.slides = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            or {
                eq('showAsBackground', false)
                isNull('showAsBackground')
            }
            eq('deleted', false)
            order('indx','asc')
        }

        model.specialSaleSlides = SpecialSaleSlide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            le('startDate', new Date())
            ge('finishDate', new Date())
            gt('remainingCount', 0)
        }

        model.background = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            eq('showAsBackground', true)
            eq('deleted', false)
            order('indx','asc')
        }?.find()

        model.pageContext = [:]
        model.pageContext["productTypes.id"] = [productType.id]

        model.title = productType.pageTitle ?: productType.name
        model.description = productType.description
        model.keywords = productType.keywords

        model.productTypeId = productType?.id
        model.productTypeName = productType?.name

        model.mostVisitedProducts = Product.createCriteria().listDistinct {
            models {
                eq('status', 'exists')
            }
            or {
                isNull('isVisible')
                eq('isVisible', true)
            }
            productTypes {
                'in'('id', productType?.allChildren?.collect { it.id } + [productType.id])
            }
            maxResults(20)
            order("visitCount", "desc")
        }

        def view = grailsApplication.config.browse.view.root
        if (productType.parentProduct)
            view = 'browse'
        else if (session.mobile)
            view = grailsApplication.config.browse.view.mobile

        render(model: model, view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/${view}");
    }

    def filter() {
        def model = [:]
        def f = params.f
        if (f instanceof String[] && f.length)
            f = f[0]
        if (!f) {
            redirect(uri: "/")
            return
        }

        model.filters = browseService.findFilteredPageFilters(f, params.sort, params.dir, params.page ?: 0, "${f} ${params.page ?: 0}")

        if (model.filters.products.productIds.size() == 0) {
            redirect(uri: "/notFound")
            return
        }

        model.commonLink = createLink(uri: '/')

        model.rootProductTypes = ProductType.findAllByParentProductIsNull()

        def brandList = new ArrayList<Brand>()
        def brand
        if (model.filters["selecteds"]["b"])
            brandList.addAll(Brand.createCriteria().list {
                'in'('id', model.filters["selecteds"]["b"])
            })
        brand = brandList.collect { it.name }.join(', ')
        if (!brand)
            brand = ''

        def productType = ProductType.get(f?.split(',').reverse()?.find {
            it.startsWith('p')
        }?.replace('p', '')?.toLong())

        model.brand = brand

        model.slides = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            or {
                eq('showAsBackground', false)
                isNull('showAsBackground')
            }
            eq('deleted', false)
            order('indx','asc')
        }

        model.specialSaleSlides = SpecialSaleSlide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            le('startDate', new Date())
            ge('finishDate', new Date())
            gt('remainingCount', 0)
        }

        model.background = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            eq('showAsBackground', true)
            eq('deleted', false)
            order('indx','asc')
        }?.find()

        trackingService.trackExplore(productType, brandList)

        if (productType) {
            if (brand != '') {
                //brand and productType def pageDetails
                def pageDetails = PageDetails.findByProductType(productType)
                if (!pageDetails)
                    pageDetails = PageDetails.findByProductTypeIsNull()
                if (pageDetails) {
                    model.title = pageDetails?.title?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
                    model.description = pageDetails?.description?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
                    model.keywords = pageDetails?.keywords?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
                } else {
                    model.title = productType?.toString()
                    if (brand && brand != "")
                        model.title = (model.title ? model.title + " - " : "") + brand

                }
            } else {
                def pageDetails = PageDetails.findByProductTypeIsNull()
                if (pageDetails) {
                    model.title = pageDetails?.title?.replace('$PRODUCTTYPE$', productType?.toString())
                    model.description = pageDetails?.description?.replace('$PRODUCTTYPE$', productType?.toString())
                    model.keywords = pageDetails?.keywords?.replace('$PRODUCTTYPE$', productType?.toString())
                } else {
                    //productType only
                    model.title = productType.pageTitle ?: productType.name
                    model.description = productType.description
                    model.keywords = productType.keywords
                }
            }

            model.articles = JournalArticle.findAllByBaseProduct productType

        } else if (brand != '') {
            //brand only
            if (brandList.count { it } == 1) {
                model.title = brandList.first()?.pageTitle ?: brandList.first()?.name
                model.description = brandList.first()?.description
                model.keywords = brandList.first()?.keywords
            } else {
                model.title = brand
            }
        } else {
            //no filter
            model.title = message(code: 'site.mainPage.title')
            model.description = message(code: 'site.mainPage.description')
            model.keywords = message(code: 'site.mainPage.keywords')
        }

        model.productTypeId = productType?.id
        model.productTypeName = productType?.name


        model.mostVisitedProducts = Product.createCriteria().listDistinct {
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
            order("visitCount", "desc")
        }

        render(model: model, view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/filter")
    }

    def ajaxFilter() {
        def model = [:]
        def f = params.f
        if (f instanceof String[] && f.length)
            f = f[0]
        if (!f) {
            redirect(uri: "/")
            return
        }

        model.filters = browseService.findFilteredPageFilters(f, params.sort, params.dir, params.page ?: 0, "${f} ${params.page ?: 0}")

        if (model.filters.products.productIds.size() == 0) {
            redirect(uri: "/notFound")
            return
        }

        model.commonLink = createLink(uri: '/')

        model.rootProductTypes = ProductType.findAllByParentProductIsNull()

        def brandList = new ArrayList<Brand>()
        def brand
        if (model.filters["selecteds"]["b"])
            brandList.addAll(Brand.createCriteria().list {
                'in'('id', model.filters["selecteds"]["b"])
            })
        brand = brandList.collect { it.name }.join(', ')
        if (!brand)
            brand = ''

        def productType = ProductType.get(f?.split(',').reverse()?.find {
            it.startsWith('p')
        }?.replace('p', '')?.toLong())

        model.brand = brand

        model.slides = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            or {
                eq('showAsBackground', false)
                isNull('showAsBackground')
            }
            eq('deleted', false)
            order('indx','asc')
        }

        model.specialSaleSlides = SpecialSaleSlide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            le('startDate', new Date())
            ge('finishDate', new Date())
            gt('remainingCount', 0)
        }

        model.background = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            eq('showAsBackground', true)
            eq('deleted', false)
            order('indx','asc')
        }?.find()

        trackingService.trackExplore(productType, brandList)

        if (productType) {
            if (brand != '') {
                //brand and productType def pageDetails
                def pageDetails = PageDetails.findByProductType(productType)
                if (!pageDetails)
                    pageDetails = PageDetails.findByProductTypeIsNull()
                if (pageDetails) {
                    model.title = pageDetails?.title?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
                    model.description = pageDetails?.description?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
                    model.keywords = pageDetails?.keywords?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
                } else {
                    model.title = productType?.toString()
                    if (brand && brand != "")
                        model.title = (model.title ? model.title + " - " : "") + brand

                }
            } else {
                def pageDetails = PageDetails.findByProductTypeIsNull()
                if (pageDetails) {
                    model.title = pageDetails?.title?.replace('$PRODUCTTYPE$', productType?.toString())
                    model.description = pageDetails?.description?.replace('$PRODUCTTYPE$', productType?.toString())
                    model.keywords = pageDetails?.keywords?.replace('$PRODUCTTYPE$', productType?.toString())
                } else {
                    //productType only
                    model.title = productType.pageTitle ?: productType.name
                    model.description = productType.description
                    model.keywords = productType.keywords
                }
            }

            model.articles = JournalArticle.findAllByBaseProduct productType

        } else if (brand != '') {
            //brand only
            if (brandList.count { it } == 1) {
                model.title = brandList.first()?.pageTitle ?: brandList.first()?.name
                model.description = brandList.first()?.description
                model.keywords = brandList.first()?.keywords
            } else {
                model.title = brand
            }
        } else {
            //no filter
            model.title = message(code: 'site.mainPage.title')
            model.description = message(code: 'site.mainPage.description')
            model.keywords = message(code: 'site.mainPage.keywords')
        }

        model.productTypeId = productType?.id
        model.productTypeName = productType?.name


        model.mostVisitedProducts = Product.createCriteria().listDistinct {
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
            order("visitCount", "desc")
        }

        def result = [:]
        result.pageTitle = model.title
        result.title = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxFilter/pageTitle", model: model).toString()
        result.products = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxFilter/products", model: model).toString()
        result.filterBar = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxFilter/filterBar", model: model).toString()
        result.graphicalFilter = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxFilter/graphicalFilter", model: model).toString()
        result.breadCrumb = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxFilter/breadCrumb", model: model).toString()
        result.pagination = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxFilter/pagination", model: model).toString()

        render(result as JSON)
    }

    def sidebar() {
        def subProductTypes = []//olapService.productTypes()
        def brands = browseService.brands()//olapService.brands()
        def breadCrumb = browseService.breadCrumb()
        def resp = [subProductTypes: subProductTypes, brands: brands, breadCrumb: breadCrumb, browsingProductTypeId: params.browsingProductTypeId, browsingBrandId: params.browsingBrandId]

        render resp as JSON
    }

    def index() {
        if (session.forwardUri) {

            trackingService.trackSignIn()

            def url = session.forwardUri
            session.forwardUri = null
            url = url.replace(request.contextPath, "")
            redirect url: url
            return
        }

        trackingService.trackExplore()

        //product type
        def productType = [
                children: ProductType.findAllByParentProductIsNullAndDeleted(false)
        ]

        def model = [productType: productType]

        model.commonLink = createLink(uri: '/browse').replace('/site', '')

        model.breadCrumb = []

        model.subProductTypeLinks = []
        def base = "${model.commonLink}/"

        productType.children.each {
            model.subProductTypeLinks << [name: it.name, href: base + it.urlName, id: it.id]
        }

        ProductType.createCriteria().listDistinct {
            ne('deleted', true)
            godFathers {
                eq('id', productType.id)
                ne('deleted', true)
            }
        }.each {
            model.subProductTypeLinks << [name: it.name, href: base + it.urlName, id: it.id]
        }

        model.rootProductTypes = ProductType.findAllByParentProductIsNull()

        model.filters = browseService.findProductTypeFilters(null, params.page ?: 0, "${params.page ?: 0}")

        //slides

        model.slides = Slide.createCriteria().list {
            eq('visibleOnFirstPage', true)
            or {
                eq('showAsBackground', false)
                isNull('showAsBackground')
            }
            eq('deleted', false)
            order('indx','asc')
        }

        model.specialSaleSlides = SpecialSaleSlide.createCriteria().list {
            eq('visibleOnFirstPage', true)
            le('startDate', new Date())
            ge('finishDate', new Date())
            gt('remainingCount', 0)
        }

        model.background = Slide.createCriteria().list {
            eq('visibleOnFirstPage', true)
            eq('showAsBackground', true)
            eq('deleted', false)
            order('indx','asc')
        }?.find()

        model.mostVisitedProducts = Product.createCriteria().listDistinct {
            models {
                eq('status', 'exists')
            }
            or {
                isNull('isVisible')
                eq('isVisible', true)
                eq('deleted', false)
            }
            maxResults(20)
            order("visitCount", "desc")
        }


        render(model: model, view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/index")
    }

    def category() {
        def productTypes = ProductType.findAllByParentProductIsNull()

        def productType = ProductType.get(params.id)
        def filterProductTypes
        if (productType)
            filterProductTypes = ProductType.findAllByParentProduct(productType)
        else
            filterProductTypes = ProductType.findAllByParentProductIsNull()

        [productTypes: productTypes, filterProductTypes: filterProductTypes]
    }

    def productQuickView() {
        if (!params.id?.toString()?.isLong()) {
            redirect(uri: "/notFound")
            return
        }

        def product = Product.get(params.id)
        if (!product || product.deleted || (product.isVisible == false)) {
            redirect(uri: "/notFound")
            return
        }
        def customerReviews = CustomerReview.findAllByProduct product
        def rate = customerReviews.count { it } == 0 ? 0 : Math.round(customerReviews.sum(0, {
            it.rate
        }) / customerReviews.count { it })

        def productModel = params.model ? ProductModel.get(params.model) : ProductModel.findByProductAndIsDefaultModel(product, true)

        render template: "/${session.mobile ? 'mobile' : 'site'}/felfel/templates/productQuickView", model: [product: product, productModel: productModel, rate: rate, quickView: true]
    }

    def productModelImages() {
        def product = Product.get(params.productId)
        if (!product) {
            render ''
            return
        }

        def models = ProductModel.findAllByProduct(product)
        def productModel
        models.each {
            def model = it
            Boolean selected = true
            product.variations.each { variation ->
                def modelVariationId = model?.variationValues.find {
                    it.variationGroup.id == variation.variationGroup.id
                }?.id?.toLong()
                def selectedVariationId = params."variation${variation.id}" ? params."variation${variation.id}" == '' ? null : params."variation${variation.id}".toLong() : null
                if (modelVariationId != selectedVariationId)
                    selected = false
            }

            if (model.guarantee?.id?.toLong() != params.guarantee?.toLong())
                selected = false

            if (selected)
                productModel = model
        }

        if (session.mobile)
            render template: "/mobile/common/product/images", model: [product: product, productModel: productModel, lazyLoad: true]
        else
            render template: "/site/${grailsApplication.config.eShop.instance}/templates/product/zoom", model: [product: product, productModel: productModel]
    }

    def product() {
        if (!params.id?.toString()?.isLong()) {
            redirect(uri: "/notFound")
            return
        }

        def product = Product.get(params.id)
        if (!product || product.deleted || (product.isVisible == false)) {
            redirect(uri: "/notFound")
            return
        }

        def productTypeList = ProductType.findAllByParentProductIsNull()

        trackingService.trackProductVisit(product)

        def model = [productTypes: productTypeList, product: product]
        model.price = priceService.calcProductPrice(product?.id)

        def customerReviews = CustomerReview.findAllByProductAndStatus(product, 'approved')
        model.rate = customerReviews.count { it } == 0 ? 0 : Math.round(customerReviews.sum(0, {
            it.rate
        }) / customerReviews.count { it })
        model.rateCount = customerReviews.count { it }
        model.commonLink = createLink(uri: '/browse')

        def productTypeChain = []
        def productTypeNavigator = product.productTypes.toArray()[0]
        while (productTypeNavigator) {
            productTypeChain << productTypeNavigator
            productTypeNavigator = productTypeNavigator.parentProduct
        }
        productTypeChain = productTypeChain.reverse()

        model.breadCrumb = []

        productTypeChain.each {
            model.breadCrumb << [name: it.name, href: "${model.commonLink}/${it.urlName}/", id: it.id]
        }

        model.rootProductTypes = ProductType.findAllByParentProductIsNull()

        model.mostVisitedProducts = Product.createCriteria().listDistinct {
            models {
                eq('status', 'exists')
            }
            or {
                isNull('isVisible')
                eq('isVisible', true)
            }
            productTypes {
                eq('id', model.breadCrumb.last().id)
            }
            maxResults(20)
            order("visitCount", "desc")
        }

        //attributes
        model.rootAttributeCategories = AttributeCategory.findAllByProductTypeAndParentCategoryIsNull(product.productTypes.toArray().first()).toList()
                .collect { [item: it, hasAttribute: false] }
//                .collect { [id: it.id, name: it.name, categories: [], attributes: []] }


        model.rootAttributeCategories.each {
            category ->
                fillAttibuteCategoryChildren(product, category)
        }

        def productModel = params.model ? ProductModel.get(params.model) : ProductModel.findByProductAndIsDefaultModel(product, true)
        if (!productModel)
            productModel = ProductModel.findByProductAndIsDefaultModel(product, true)
        if (!params.model && productModel?.status != 'exists') {
            def newModel = ProductModel.findByProductAndStatus(product, 'exists')
            if (newModel)
                productModel = newModel
        }
        model.productModel = productModel
        model.addedValues = AddedValue.findAllByBaseProductAndDeletedNotEqual(product, true).findAll { addedValue ->
            !addedValue.variationValues.any { variationValue ->
                variationValue.value != productModel.variationValues
                        .find { it.variationGroup.id == variationValue.variationGroup.id }?.value
            }
        }
        model.selectedAddedValues = model.addedValues.findAll {
            it.processTime == 'mandetory' || params.selectedAddedValues?.toString()?.split(',')?.contains(it.id.toString())
        }

        //update product visit count

        try {
            if (!product.visitCount)
                product.visitCount = 0;
            product.visitCount++;
            product.isSynchronized = false
            product.save()
        } catch (x) {
            x.printStackTrace()
        }
        //fill zoomable property of images
        if (product.mainImage) {
            try {
                imageService.getImageSize(product.mainImage, product)
                product.mainImage.dynamicProperties.zoomable = product.mainImage.dynamicProperties.width >= 700 || product.mainImage.dynamicProperties.height >= 700
            } catch (e) {
            }
        }

        product?.images?.findAll { it?.id != product?.mainImage?.id }?.each {
            try {
                imageService.getImageSize(it, product)
                it.dynamicProperties.zoomable = it.dynamicProperties.width >= 700 || it.dynamicProperties.height >= 700
            } catch (e) {
            }
        }

        //update last visited products
        def lastVisitedProducts
        synchronized (this.getClass()) {
            lastVisitedProducts = session.getAttribute('lastVisitedProducts')
            if (!lastVisitedProducts) {
                lastVisitedProducts = []
                String lastVisitedProductsStr = cookie(name: 'lastVisitedProducts')
                if (lastVisitedProductsStr)
                    lastVisitedProducts = lastVisitedProductsStr.split(",").toList()
            }
            if (!lastVisitedProducts.contains(params.id))
                lastVisitedProducts.add(params.id)
            session.setAttribute('lastVisitedProducts', lastVisitedProducts)
        }

        if (lastVisitedProducts) {
            Cookie cookie = new Cookie("lastVisitedProducts", lastVisitedProducts.join(","))
            cookie.maxAge = 100
            response.addCookie(cookie)
        }

        def modelNames = [product.name]
        product.models.each { if (!modelNames.contains(it.name)) modelNames << it.name }
        def title = product.toString()
        if (product.name)
            title = title.replace(product.name, modelNames.unique { it?.trim() }.join(','))
        model.title = title
        model.description = message(code: 'site.product.page.description', args: [title])
        model.showHistogram = true

        render(model: model, view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/product")
    }

    def productCard() {
        def product = Product.get(params.productId)
        if (!product) {
            render ''
            return
        }

        def models = ProductModel.findAllByProduct(product)
        def productModel
        models.each {
            def model = it
            Boolean selected = true
            product.variations.each { variation ->
                def modelVariationId = model?.variationValues.find {
                    it.variationGroup.id == variation.variationGroup.id
                }?.id?.toLong()
                def selectedVariationId = params."variation${variation.id}" ? params."variation${variation.id}" == '' ? null : params."variation${variation.id}".toLong() : null
                if (modelVariationId != selectedVariationId)
                    selected = false
            }

            if (model.guarantee?.id?.toLong() != params.guarantee?.toLong())
                selected = false

            if (selected)
                productModel = model
        }

        def addedValues = AddedValue.findAllByBaseProductAndDeletedNotEqual(product, true).findAll { addedValue ->
            !addedValue?.variationValues.any { variationValue ->
                variationValue.value != productModel?.variationValues
                        .find { it.variationGroup.id == variationValue.variationGroup.id }?.value
            }
        }

        def selectedAddedValues = addedValues.findAll {
            it.processTime == 'mandetory' || params.selectedAddedValues?.toString()?.split(',')?.contains(it.id.toString())
        }

        if (session.mobile)
            render(template: "/mobile/common/product/actions", model: [product: product, productModel: productModel, addedValues: addedValues, selectedAddedValues: selectedAddedValues, showHistogram: true])
        else
            render(template: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/templates/product/card", model: [product: product, productModel: productModel, addedValues: addedValues, selectedAddedValues: selectedAddedValues, showHistogram: true])
    }

    def productShoppingPanel() {

        def product = Product.get(params.productId)

        if (!product) {
            render ''
            return
        }

        def models = ProductModel.findAllByProduct(product)
        def productModel
        models.each {
            def model = it
            Boolean selected = true
            product.variations.each { variation ->
                def modelVariationId = model?.variationValues.find {
                    it.variationGroup.id == variation.variationGroup.id
                }?.id?.toLong()
                def selectedVariationId = params."variation${variation.id}" ? params."variation${variation.id}" == '' ? null : params."variation${variation.id}".toLong() : null
                if (modelVariationId != selectedVariationId)
                    selected = false
            }

            if (model.guarantee.id.toLong() != params.guarantee.toLong())
                selected = false

            if (selected)
                productModel = model
        }

        def addedValues = AddedValue.findAllByBaseProductAndDeletedNotEqual(product, true).findAll { addedValue ->
            !addedValue?.variationValues.any { variationValue ->
                variationValue.value != productModel?.variationValues
                        .find { it.variationGroup.id == variationValue.variationGroup.id }?.value
            }
        }

        def selectedAddedValues = addedValues.findAll {
            it.processTime == 'mandetory' || params.selectedAddedValues?.toString()?.split(',')?.contains(it.id.toString())
        }

        render(template: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/templates/product/shoppingPanel", model: [product: product, productModel: productModel, addedValues: addedValues, selectedAddedValues: selectedAddedValues])
    }

    def productAdditives() {
        def product = Product.get(params.productId)

        def models = ProductModel.findAllByProduct(product)
        def productModel
        models.each {
            def model = it
            Boolean selected = true
            product.variations.each { variation ->
                def modelVariationId = model?.variationValues.find {
                    it.variationGroup.id == variation.variationGroup.id
                }?.id?.toLong()
                def selectedVariationId = params."variation${variation.id}" ? params."variation${variation.id}" == '' ? null : params."variation${variation.id}".toLong() : null
                if (modelVariationId != selectedVariationId)
                    selected = false
            }

            if (model?.guarantee?.id?.toLong() != params?.guarantee?.toLong())
                selected = false

            if (selected)
                productModel = model
        }

        def addedValues = AddedValue.findAllByBaseProductAndDeletedNotEqual(product, true).findAll { addedValue ->
            !addedValue?.variationValues?.any { variationValue ->
                variationValue.value != productModel?.variationValues
                        .find { it?.variationGroup?.id == variationValue?.variationGroup?.id }?.value
            }
        }

        def selectedAddedValues = addedValues.findAll {
            it.processTime == 'mandetory' || params.selectedAddedValues?.toString()?.split(',')?.contains(it.id.toString())
        }

        render(template: 'product/additives', model: [product: product, productModel: productModel, addedValues: addedValues, selectedAddedValues: selectedAddedValues, price: (productModel ? priceService.calcProductModelPrice(productModel.id) : 0)])
    }

    def productImages() {
        def product = Product.get(params.productId)
        if (!product) {
            render ''
            return
        }

        def models = ProductModel.findAllByProduct(product)
        def productModel
        models.each {
            def model = it
            Boolean selected = true
            product.variations.each { variation ->
                def modelVariationId = model?.variationValues.find {
                    it.variationGroup.id == variation.variationGroup.id
                }?.id?.toLong()
                def selectedVariationId = params."variation${variation.id}" ? params."variation${variation.id}" == '' ? null : params."variation${variation.id}".toLong() : null
                if (modelVariationId != selectedVariationId)
                    selected = false
            }

            if (model?.guarantee?.id?.toLong() != params?.guarantee?.toLong())
                selected = false

            if (selected)
                productModel = model
        }

//        def addedValues = AddedValue.findAllByBaseProductAndDeletedNotEqual(product, true).findAll { addedValue ->
//            !addedValue?.variationValues?.any { variationValue ->
//                variationValue.value != productModel?.variationValues
//                        .find { it?.variationGroup?.id == variationValue?.variationGroup?.id }?.value
//            }
//        }

//        def selectedAddedValues = addedValues.findAll {
//            it.processTime == 'mandetory' || params.selectedAddedValues?.toString()?.split(',')?.contains(it.id.toString())
//        }

        render(template: "${grailsApplication.config.eShop.instance}/templates/product/zoom",
                model: [product     : product,
                        productModel: productModel])
    }

    def productPrice() {
        def product = Product.get(params.productId)

        def models = ProductModel.findAllByProduct(product)
        def productModel
        models.each {
            def model = it
            Boolean selected = true
            product.variations.each { variation ->
                def modelVariationId = model.variationValues.find {
                    it.variationGroup.id == variation.variationGroup.id
                }?.id?.toLong()
                def selectedVariationId = params."variation${variation.id}" ? params."variation${variation.id}" == '' ? null : params."variation${variation.id}".toLong() : null
                if (modelVariationId != selectedVariationId)
                    selected = false
            }

            if (model?.guarantee?.id?.toLong() != params?.guarantee?.toLong())
                selected = false

            if (selected)
                productModel = model
        }

        if (session.mobile)
            render(template: '/mobile/common/product/card', model: [product: product, productModel: productModel])
        else
            render(template: 'product/price', model: [product: product, productModel: productModel])
    }

    def productImage() {
        def product = Product.get(params.id)

        if (!product) {
            render ''
            return
        }
        if (product.mainImage)
            imageService.getImageSize(product.mainImage, product)

        product?.images?.findAll { it?.id != product?.mainImage?.id }?.each {
            imageService.getImageSize(it, product)
        }

        render(template: "productImages", model: [product: product, selectedImage: params.img ? product.images.find {
            it.id.toString() == params.img.toString()
        } : product.mainImage])
    }

    def fillAttibuteCategoryChildren(Product product, parentCategory) {

        parentCategory.attributes = product.attributes.findAll {
            attr ->
                (attr?.attributeType?.category?.id == parentCategory.item.id
                        && !attr?.attributeType?.deleted
                        && (attr?.attributeType?.showPositions?.contains('productDetails')
                        || attr?.attributeType?.showPositions?.contains('productFullDetails'))
                        && attr?.value
                        && attr?.value?.toString()?.compareTo("N/A") != 0)
        }
        if (parentCategory.attributes)
            parentCategory.hasAttribute = parentCategory.attributes.count { it } > 0

        parentCategory.childCategories = AttributeCategory.findAllByParentCategory(parentCategory.item).collect {
            [item: it, hasAttribute: false]
        }
        parentCategory.childCategories.each { childCategory ->
            fillAttibuteCategoryChildren(product, childCategory)
            if (childCategory.hasAttribute)
                parentCategory.hasAttribute = true
        }
    }

    def image() {
        redirect(controller: "image", params: params)
    }

    def searchRow() {

    }

    def productAbstract() {
        def productId = 3
        def product = Product.get(productId)
        def model = [product: product]
        model << priceService.calcProductPrice(productId)

        render(template: "product_search", model: model)
    }

    def flushChanges() {
        def sql = new Sql(dataSource)
        sql.execute("CALL `populate_product_closure`()")
        render "OK"
    }

    def reindex() {
        searchableService.unindexAll()
        searchableService.reindexAll()
        render 'reindex completed.'
    }

    def search() {

        def model = [:]
        def productIdList = []
        def productModelIdList = []

        if (!params.phrase) {
            render ''
            return
        }
        if (params.phrase) {
            def query = params.phrase.toString().trim()
            query = FarsiNormalizationFilter.normalize(query.toCharArray(), query.length())
            while (query.contains('  '))
                query = query.replace('  ', ' ')
            query = "*${query.replace(' ', '* *')}*"
            BooleanQuery.setMaxClauseCount(100000);
            productIdList = Product.search({
                queryString(query)
            },
                    [reload: false, max: 1000])
            productModelIdList = ProductModel.search({
                queryString(query)
            },
                    [reload: false, max: 1000])
        }
        if (!params.f)
            params.f = 'p0'
        def f = params.f
        if (f instanceof String[] && f.length) {
            redirect(action: 'search', params: [f: f.join(','), phrase: params.phrase])
            return
        }

        def productIds = productIdList.results.collect {
            it.id
        } + ProductModel.findAllByIdInList(productModelIdList.results.collect { it?.id })?.collect {
            it?.product?.id
        }
        model.filters = browseService.findSearchPageFilters(productIds, f, params.sort, params.dir, params.page ?: 0, "${productIdList.results.collect { it.id }} ${f} ${params.page ?: 0}")
        //sort result again
        model.filters.products.productIds.sort {
            def id = it
            def productIndex = productIdList.results.findIndexOf { it.id == id }
            -(productIndex >= 0 ? productIdList.scores[productIndex] : 0)
        }
        model.commonLink = createLink(uri: '/')

        model.rootProductTypes = ProductType.findAllByParentProductIsNull()

        def brandList = new ArrayList<Brand>()
        def brand
        if (model.filters["selecteds"]["b"])
            brandList.addAll(Brand.createCriteria().list {
                'in'('id', model.filters["selecteds"]["b"])
            })
        brand = brandList.collect { it.name }.join(', ')
        if (!brand)
            brand = ''

        def productType = ProductType.get(f?.split(',').reverse()?.find {
            it.startsWith('p')
        }?.replace('p', '')?.toLong())

        model.slides = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            or {
                eq('showAsBackground', false)
                isNull('showAsBackground')
            }
            eq('deleted', false)
            order('indx','asc')
        }

        model.background = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            eq('showAsBackground', true)
            eq('deleted', false)
            order('indx','asc')
        }?.find()

        trackingService.trackSearch(productType, brandList, params.phrase)

        def pageDetails
        if (productType && brand != '')
            pageDetails = PageDetails.findByProductType(productType)
        if (!pageDetails)
            pageDetails = PageDetails.findByProductTypeIsNull()
        if (pageDetails)
            model.title = pageDetails?.title?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
        else {
            model.title = (productType ? productType.toString() + " - " : "") + params.phrase
            if (brand && brand != '')
                model.title = (model.title ? model.title + " - " : "") + brand + ' ' + params.phrase
        }
        model.description = pageDetails?.description?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
        model.keywords = pageDetails?.keywords?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())

        model.productTypeId = productType?.id
        model.productTypeName = productType?.name

        render(view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/search", model: model)
    }

    def ajaxSearch() {

        def model = [:]
        def productIdList = []
        def productModelIdList = []

        if (!params.phrase) {
            render ''
            return
        }
        if (params.phrase) {
            def query = params.phrase.toString().trim()
            query = FarsiNormalizationFilter.normalize(query.toCharArray(), query.length())
            while (query.contains('  '))
                query = query.replace('  ', ' ')
            query = "*${query.replace(' ', '* *')}*"
            BooleanQuery.setMaxClauseCount(10000);
            productIdList = Product.search({
                queryString(query)
            },
                    [reload: false, max: 1000])
            productModelIdList = ProductModel.search({
                queryString(query)
            },
                    [reload: false, max: 1000])
        }
        if (!params.f)
            params.f = 'p0'
        def f = params.f
        if (f instanceof String[] && f.length) {
            redirect(action: 'search', params: [f: f.join(','), phrase: params.phrase])
            return
        }

        def productIds = productIdList.results.collect {
            it.id
        } + ProductModel.findAllByIdInList(productModelIdList.results.collect { it?.id })?.collect {
            it?.product?.id
        }
        model.filters = browseService.findSearchPageFilters(productIds, f, params.sort, params.dir, params.page ?: 0, "${productIdList.results.collect { it.id }} ${f} ${params.page ?: 0}")
        //sort result again
        model.filters.products.productIds.sort {
            def id = it
            def productIndex = productIdList.results.findIndexOf { it.id == id }
            -(productIndex >= 0 ? productIdList.scores[productIndex] : 0)
        }
        model.commonLink = createLink(uri: '/')

        model.rootProductTypes = ProductType.findAllByParentProductIsNull()

        def brandList = new ArrayList<Brand>()
        def brand
        if (model.filters["selecteds"]["b"])
            brandList.addAll(Brand.createCriteria().list {
                'in'('id', model.filters["selecteds"]["b"])
            })
        brand = brandList.collect { it.name }.join(', ')
        if (!brand)
            brand = ''

        def productType = ProductType.get(f?.split(',').reverse()?.find {
            it.startsWith('p')
        }?.replace('p', '')?.toLong())

        model.slides = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            or {
                eq('showAsBackground', false)
                isNull('showAsBackground')
            }
            eq('deleted', false)
            order('indx','asc')
        }

        model.background = Slide.createCriteria().list {
            if (productType) {
                productTypes {
                    eq('id', productType.id)
                }
            } else {
                eq('visibleOnFirstPage', true)
            }
            eq('showAsBackground', true)
            eq('deleted', false)
            order('indx','asc')
        }?.find()

        trackingService.trackSearch(productType, brandList, params.phrase)

        def pageDetails
        if (productType && brand != '')
            pageDetails = PageDetails.findByProductType(productType)
        if (!pageDetails)
            pageDetails = PageDetails.findByProductTypeIsNull()
        if (pageDetails)
            model.title = pageDetails?.title?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
        else {
            model.title = (productType ? productType.toString() + " - " : "") + params.phrase
            if (brand && brand != '')
                model.title = (model.title ? model.title + " - " : "") + brand + ' ' + params.phrase
        }
        model.description = pageDetails?.description?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())
        model.keywords = pageDetails?.keywords?.replace('$BRAND$', brand)?.replace('$PRODUCTTYPE$', productType?.toString())

        model.productTypeId = productType?.id
        model.productTypeName = productType?.name


        def result = [:]
        result.pageTitle = model.title
        result.title = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxSearch/pageTitle", model: model).toString()
        result.products = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxSearch/products", model: model).toString()
        result.filterBar = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxSearch/filterBar", model: model).toString()
        result.graphicalFilter = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxSearch/graphicalFilter", model: model).toString()
        result.breadCrumb = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxSearch/breadCrumb", model: model).toString()
        result.pagination = g.render(template: "${grailsApplication.config.eShop.instance}/ajaxSearch/pagination", model: model).toString()

        render(result as JSON)
    }

    def searchAutoComplete() {

        def model = [:]
        def productIdList = []
        def productModelIdList = []

        if (!params.phrase) {
            render ''
            return
        }
        if (params.phrase) {
            def query = params.phrase.toString().trim()
            query = FarsiNormalizationFilter.normalize(query.toCharArray(), query.length())
            while (query.contains('  '))
                query = query.replace('  ', ' ')
            query = "*${query.replace(' ', '* *')}*"
            try {
                BooleanQuery.setMaxClauseCount(10000);
                productIdList = Product.search({
                    queryString(query)
                },
                        [reload: false, max: 1000])
                productModelIdList = ProductModel.search({
                    queryString(query)
                },
                        [reload: false, max: 1000])
            }catch (e){}
        }
        if (!params.f)
            params.f = 'p0'
        def f = params.f
        if (f instanceof String[] && f.length) {
            redirect(action: 'searchAutoComplete', params: [f: f.join(','), phrase: params.phrase])
            return
        }

        def productIds = productIdList.results.collect {
            it.id
        } + ProductModel.findAllByIdInList(productModelIdList.results.collect { it?.id })?.collect {
            it?.product?.id
        }

        model.productIds = browseService.findSearchPageFilters(productIds, f, null, null, params.page ?: 0, "${productIdList.results.collect { it.id }} ${f} ${params.page ?: 0}").products.productIds
        //sort result again
        model.productIds.sort {
            def id = it
            def productIndex = productIdList.results.findIndexOf { it.id == id }
            -(productIndex >= 0 ? productIdList.scores[productIndex] : 0)
        }
        model.commonLink = createLink(uri: '/')

        if (model.productIds?.size() > 0)
            render(template: 'search_autoComplete', model: model)
        else
            render ''
    }

    def getAllChildProductTypes(ProductType productType) {

        def result = [productType]

        productType.children.each {
            result += getAllChildProductTypes(it)
        }

        result
    }

    def article() {
        def model = [:]

        model.article = JournalArticle.get(params.id)
        if (!model.article) {
            redirect(uri: "/notFound")
            return
        }


        model.productType = model.article.baseProduct as ProductType
        model.articles = JournalArticle.findAllByBaseProduct model.productType

        model.commonLink = createLink(uri: '/browse')

        def productTypeChain = []
        def productTypeNavigator = model.productType
        while (productTypeNavigator) {
            productTypeChain << productTypeNavigator
            productTypeNavigator = productTypeNavigator.parentProduct
        }
        productTypeChain = productTypeChain.reverse()

        model.breadCrumb = []

        productTypeChain.each {
            model.breadCrumb << [name: it.name, href: "${model.commonLink}/${it.urlName}/"]
        }

        model.subProductTypeLinks = []
        def base = "${model.commonLink}/"

        model.productType.children.findAll { !it.deleted }.each {
            model.subProductTypeLinks << [name: it.name, href: base + it.urlName, id: it.id]
        }

        def productType = model.article.baseProduct
        ProductType.createCriteria().listDistinct {
            ne('deleted', true)
            godFathers {
                eq('id', model.productType.id)
                ne('deleted', true)
            }
        }.each {
            model.subProductTypeLinks << [name: it.name, href: base + it.urlName, id: it.id]
        }

        render(model: model, view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/article")
    }

    def articleList() {
        def model = [:]

        model.productType = ProductType.get(params.id)

        model.articles = JournalArticle.createCriteria().list(max: 10, offset: (params.page ? params.page.toInteger() * 10 : 0), {
            if (params.id)
                'in'('baseProduct.id', ProductType.get(params.id).allChildren.collect { it.id } + [params.id.toLong()])
            order('id', ORDER_DESCENDING)

            maxResults(10)
        })

        model.totalPages = JournalArticle.createCriteria().count {
            if (params.id)
                'in'('baseProduct.id', ProductType.get(params.id).allChildren.collect { it.id } + [params.id.toLong()])
        }.toDouble() / 10

        render(model: model, view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/articleList")
    }

    def contactUs() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/contact_us"
    }

    def sendMail() {

        if (!simpleCaptchaService.validateCaptcha(params.captcha)) {

            flash.message = message(code: 'contactUs.email.invalidCaptcha')
            redirect(uri: '/contactUs')
            return
        }

        mailService.sendMail {
            to params.department
            subject "${message(code: 'contactUs.email.subject')}"
            html(view: "/messageTemplates/mail/contactus",
                    model: [
                            firstName: params.firstName,
                            lastName : params.lastName,
                            email    : params.email,
                            phone    : params.phone,
                            body     : params.body
                    ])
        }


        mailService.sendMail {
            to params.email
            subject message(code: 'emailTemplates.contact_us.subject')
            html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                    model: [message: g.render(template: '/messageTemplates/mail/contact_us', model: [parameters: params]).toString()])
        }

        if (params.phone)
            messageService.sendMessage(
                    params.phone,
                    g.render(template: '/messageTemplates/sms/contact_us', model: [parameters: params]).toString())


        flash.message = message(code: 'contactUs.email.successMessage')
        redirect(uri: '/contactUs')
    }

    def contactUsNewProducts() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/contact_us_new_product"
    }

    def sendMailNewProducts() {

        if (!simpleCaptchaService.validateCaptcha(params.captcha)) {

            flash.message = message(code: 'contactUs.email.invalidCaptcha')
            redirect(uri: '/contactUsNewProducts')
            return
        }

        mailService.sendMail {
            to grailsApplication.config.contactUsNewProductEmail
            subject "${message(code: 'contactUsNewProduct.email.subject')}"
            html(view: "/messageTemplates/mail/contactus",
                    model: [
                            firstName: params.firstName,
                            lastName : params.lastName,
                            email    : params.email,
                            phone    : params.phone,
                            body     : params.body
                    ])
        }


        mailService.sendMail {
            to params.email
            subject message(code: 'contactUsNewProduct.email.subject')
            html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                    model: [message: g.render(template: '/messageTemplates/mail/contact_us', model: [parameters: params]).toString()])
        }

        if (params.phone)
            messageService.sendMessage(
                    params.phone,
                    g.render(template: '/messageTemplates/sms/contact_us', model: [parameters: params]).toString())


        flash.message = message(code: 'contactUs.email.successMessage')
        redirect(uri: '/contactUsNewProducts')
    }

    def termsAndConditions() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/rights_and_laws"
    }

    def aboutUs() {

        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/about_us"
    }

    def moneyBackConditions() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/money_back_conditions"
    }

    def guarantee() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/guarantee"
    }

    def addedValue() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/added_value"
    }

    def deliveryPrice() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/delivery_price"
    }

    def trust() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/trust"
    }

    def shoppingRules() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/shoppingRules"
    }

    def customerRights() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/customerRights"
    }

    def shoppingSteps() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/shoppingSteps"
    }

    def paymentAndDelivery() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/paymentAndDelivery"
    }

    def deliveryTips() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/deliveryTips"
    }

    def paymentMethods() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/paymentMethods"
    }

    def suppliers() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/suppliers"
    }

    def goldenGuarantee() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/goldenGuarantee"
    }

    def personalEvent_help() {
        render view: "/${session.mobile ? 'mobile' : 'site'}/${grailsApplication.config.eShop.instance}/statics/personalEventHelp"
    }

    def synchMongoItem() {
        try {
//            println('synchronizing request received for ' + params.id)
            mongoService.storeProduct(Product.get(params.id))
            render "0"
        }
        catch (ex) {
            ex.printStackTrace()
            render "-1"
        }
    }

    def productStatusFilter() {
        session['status_filter'] = params.value == 'on'
        render ''
    }
    def alexaGooleAnalitic(){
        render template: "/layouts/${grailsApplication.config.eShop.instance}/alexaGooleAnalitic"
    }
    def enamad(){
        render template: "banners/enamad"
    }
}
