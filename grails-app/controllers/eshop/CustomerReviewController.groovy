package eshop

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.JSON

class CustomerReviewController {

    def springSecurityService

//    static allowedMethods = [save: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def form() {
        def customerReviewInstance
        if (params.id)
            customerReviewInstance = CustomerReview.get(params.id)
        else
            customerReviewInstance = new CustomerReview()
        render(template: "form", model: [customerReviewInstance: customerReviewInstance])
    }

    def list() {
    }

    def show(){
        [customerReviewInstance: CustomerReview.get(params.id)]
    }

    def save() {
        if(!params.id && !params.productId)
        {
            render ''
            return
        }
        def customerReviewInstance
        if (params.id) {
            customerReviewInstance = CustomerReview.get(params.id)
            customerReviewInstance.properties = params
        } else {
            def product = Product.get(params.productId)
            customerReviewInstance = new CustomerReview()
            customerReviewInstance.product = product
            customerReviewInstance.title = params.reviewTitle
            if (params.parentReviewId.toInteger() == 0)
                customerReviewInstance.rate = Integer.parseInt(params.reviewRate)
            else
                customerReviewInstance.rate = 0
            customerReviewInstance.body = params."reviewBody_${params.parentReviewId}"
            customerReviewInstance.creationDate = new Date()
            customerReviewInstance.lastUpdate = new Date()
            customerReviewInstance.parentReview = CustomerReview.get(params.parentReviewId)
            customerReviewInstance.user = springSecurityService.currentUser
            customerReviewInstance.status = 'waiting'
        }
        if(grailsApplication.config.hideCustomerReviewTitle && !customerReviewInstance.title)
            customerReviewInstance.title='-'
        if (customerReviewInstance.validate() && customerReviewInstance.save()) {
            def template='show'
            if(grailsApplication.config.customCustomerReviewTemplate)
                template="/site/${grailsApplication.config.eShop.instance}/templates/product/review"
            render(template: template, model: [customerReviewInstance: customerReviewInstance])
        } else
            render customerReviewInstance.errors
    }

    def investigate() {
        def customerReviewInstance = CustomerReview.get(params.review.id)
        customerReviewInstance.status = params.status
        customerReviewInstance.save()
        redirect(action: 'show', params:[id: params.review.id])
    }


    def delete() {
        def customerReviewInstance = CustomerReview.get(params.id)
        customerReviewInstance.delete(flush: true)
        render 0
    }

    def create() {
        render(template: 'create')
    }

    def vote() {
        if (springSecurityService.loggedIn) {
            def customerReview = CustomerReview.get(params.parentReviewId)
            if (CustomerReviewVote.findByUserAndCustomerReview(springSecurityService.currentUser, customerReview)) {
                render message(code: 'review.vote.repetitiveUser')
            } else {
                def voteInstance = new CustomerReviewVote()
                voteInstance.customerReview = customerReview
                voteInstance.user = springSecurityService.currentUser
                voteInstance.value = params.voteValue.toInteger()
                voteInstance.save()

                render voteInstance.customerReview.totalVotes
            }
        } else {
            render message(code: 'review.vote.loginRequired')
        }
    }
}
