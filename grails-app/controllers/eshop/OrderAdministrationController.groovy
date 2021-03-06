package eshop

import eshop.accounting.Account
import eshop.accounting.CustomerTransaction
import eshop.accounting.OnlinePayment
import eshop.accounting.PaymentRequest
import eshop.accounting.Transaction
import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class OrderAdministrationController {

    def priceService
    def springSecurityService
    def mongoService
    def orderTrackingService
    def deliveryService
    def pdfService
    def mailService
    def messageService
    def grailsApplication

    def orderNotification() {
        def order = Order.get(params.id)
        def user
        if(params.userName)
            user = User.findByUsername(params.userName)

        if (order && user && orderTrackingService.checkIfOrderIsVisibleToUser(order, user)) {
            String result = ""
            order.items.findAll { !it.deleted }.each {
                result += it.productModel.toString()
                result += "<br/>"
            }
            result += "<a target='_blank' href='${createLink(controller: 'orderAdministration', action: 'console', params: [id: order.id])}'>${message(code: 'order.notification.link')}</a>"
            render([title: message(code: "order.statusChangeNotification.title.${order.status}"), body: result] as JSON)
        } else
            render 0
    }


    def correctOrderActionDays() {
        println 'salam'
        return Order.list().each { order ->
            order.lastActionDate = OrderTrackingLog.findAllByOrder(order)?.sort { -it.id }?.find()?.date
            println order.save(flush: true)
        }
        render 0
    }

    @Secured([RoleHelper.ROLE_VENDOR])
    def console() {

    }

    def list() {
        def orderList = orderTrackingService.filterOrderListForUser(springSecurityService.currentUser, params.status)
        //orderTrackingService.filterOrderListForUser(params.status == 'All' ? Order.findAll() : Order.findAllByStatus(params.status), springSecurityService.currentUser)
        render view: '/orderAdministration/list', model: [orderList: orderList.size() > 0 ? orderList : [0]]
    }

    def act() {
        def order = Order.get(params.id)
        def modified = false
        if (order.paymentType == 'in-place' || order.paymentType == 'account-value')
            if (OnlinePayment.countByOrder(order) > 0 || PaymentRequest.countByOrder(order) > 0)
                modified = true
        if (order.paymentType == 'bank-receipt')
            if (OnlinePayment.countByOrder(order) > 0 || PaymentRequest.countByOrder(order) > 1)
                modified = true
        if (order.paymentType == 'online')
            if (OnlinePayment.countByOrder(order) > 1 || PaymentRequest.countByOrder(order) > 0)
                modified = true
        def availableActions = []
        switch (order.status) {
            case OrderHelper.STATUS_CREATED:
                availableActions = [OrderHelper.ACTION_MARK_AS_INCORRECT, OrderHelper.ACTION_APPROVE]
                break
            case OrderHelper.STATUS_UPDATING:
                if (grailsApplication.config.payOnCheckout)
                    availableActions = [OrderHelper.ACTION_APPROVE_PAYMENT, OrderHelper.ACTION_MARK_AS_NOT_EXIST]
                else
                    availableActions = [OrderHelper.ACTION_INQUIRY, OrderHelper.ACTION_MARK_AS_NOT_EXIST]
                break
            case OrderHelper.STATUS_NOT_EXIST:
                availableActions = [OrderHelper.ACTION_RE_APPROVE]
                break
            case OrderHelper.STATUS_PAID:
                availableActions = [OrderHelper.ACTION_APPROVE_PAYMENT, OrderHelper.ACTION_DECLINE_PAYMENT, OrderHelper.ACTION_INQUIRY]
                break
            case OrderHelper.STATUS_PAYMENT_APPROVED:
                availableActions = [OrderHelper.ACTION_TRANSMISSION]
                break
            case OrderHelper.STATUS_TRANSMITTED:
                availableActions = [OrderHelper.ACTION_DELIVERY]
                break
        }

        model:
        [
                order   : order,
                actions : availableActions,
                modified: modified
        ]
    }

    def updatePrice() {
        if (grailsApplication.config.onlyUpdateInFactor) {
            def orderItem = OrderItem.get(params.orderItem.id)

            orderItem.unitPrice = params.int('price')
            orderItem.totalPrice = orderItem.orderCount * (orderItem.unitPrice - orderItem.discount + orderItem.tax)
            orderItem.save()
            def order = orderItem.order
            if (grailsApplication.config.disableRoundingPrices) {
                order.totalPrice = Math.round(((OrderItem.findAllByOrderAndDeleted(order, false).sum(0, {
                    it.totalPrice
                }) as Integer) + order.deliveryPrice))
            } else {
                order.totalPrice = Math.round(((OrderItem.findAllByOrderAndDeleted(order, false).sum(0, {
                    it.totalPrice
                }) as Integer) + order.deliveryPrice) / 1000) * 1000
            }
            order.totalPayablePrice = order.totalPrice - order.usedAccountValue
            order.save()
        } else {
            //save new price
            def priceInstance = new Price(params)
            priceInstance.productModel = ProductModel.get(params.productModel.id)
            def lastPrice = Price.findByProductModelAndEndDateIsNull(priceInstance.productModel)
            if (lastPrice) {
                lastPrice.endDate = new Date()
                lastPrice.save()
            }

            priceInstance.startDate = new Date()
            priceInstance.rialPrice = priceInstance.currency ? priceInstance.price * priceInstance.currency.exchangeRate : priceInstance.price
            priceInstance.save()
            priceInstance.productModel.lastcalcDate=null
            priceInstance.productModel.save()
            priceInstance.productModel.product.isSynchronized = false
            priceInstance.productModel.product.save()

            //update order
            priceService.updateOrderPrice(OrderItem.get(params.orderItem.id).order)
        }
        redirect(action: 'act', params: [id: params.order.id])
    }

    def updateCount() {

        //save new price
        def orderItem = OrderItem.get(params.orderItem.id)
        orderItem.orderCount = params.count.toInteger()
        orderItem.save()

        //update order
        priceService.updateOrderPrice(orderItem.order)

        redirect(action: 'act', params: [id: params.order.id])
    }

    def updateProductModelStatus() {

        def productModel = ProductModel.get(params.productModel.id)
        productModel.status = params.status
        productModel.save()
        productModel.product.isSynchronized = false
        productModel.product.save()

        //update order
        priceService.updateOrderPrice(OrderItem.get(params.orderItem.id).order)

        redirect(action: 'act', params: [id: params.order.id])
    }

    def correctPayment() {

        def order = Order.get(params.order.id)

        //save new request
        def paymentRequest = new PaymentRequest()
        paymentRequest.value = params.value.toInteger()
        paymentRequest.trackingCode = params.trackingCode
        paymentRequest.creationDate = params.date
        paymentRequest.owner = order.customer
        paymentRequest.account = Account.get(params.account)
        paymentRequest.order = order
        paymentRequest.usingCustomerAccountValueAllowed = false
        paymentRequest.save()

        order.status = OrderHelper.STATUS_PAID
        order.paymentType = 'bank-receipt'
        order.save()

        //save order tracking log
        def trackingLog = new OrderTrackingLog()
        trackingLog.action = OrderHelper.ACTION_PAYMENT_CORRECTION
        trackingLog.date = new Date()
        trackingLog.order = order
        trackingLog.user = springSecurityService.currentUser as User
        trackingLog.title = "order.actions.${OrderHelper.ACTION_PAYMENT_CORRECTION}"
        trackingLog.description = """
                ${params.description}
                ${message(code: 'payment.type')}: ${message(code: 'payment.types.bankReceipt')}
                ${message(code: 'order.usedAccountValue')}: ${
            formatNumber(number: order.usedAccountValue, type: 'number')
        } ${eshop.currencyLabel()}
                ${message(code: 'order.payment.bank')}: ${
            message(code: "account.${paymentRequest.account.bankName}.${paymentRequest.account.type}")
        }
                ${message(code: 'order.payment.value')}: ${
            formatNumber(number: paymentRequest.value, type: 'number')
        } ${eshop.currencyLabel()}
                ${message(code: 'order.payment.trackingCode')}: ${paymentRequest.trackingCode}
                ${message(code: 'order.payment.date')}: ${rg.formatJalaliDate(date: paymentRequest.creationDate)}
"""
        trackingLog.save()

        redirect(action: 'act', params: [id: params.order.id])
    }

    def saveActionDescription()
    {

        def order = Order.get(params.order.id) //save order tracking log
        def trackingLog = new OrderTrackingLog()
        trackingLog.action = OrderHelper.ACTION_LOG
        trackingLog.date = new Date()
        trackingLog.order = order
        trackingLog.user = springSecurityService.currentUser as User
        trackingLog.title = "order.actions.custom"
        trackingLog.description = params.description
        trackingLog.save()

        redirect(action: 'act', params: [id: params.order.id])
    }

    def actOnOrder(oldStatus, newStatus, action, description) {
        def order = Order.get(params.id)
        order.status = newStatus
        order.save()
        if (grailsApplication.config.payOnCheckout && order.paymentType == 'online' && order.paymentDone) {

            //poolo bargardoon be golbon
            if (newStatus in [OrderHelper.STATUS_NOT_EXIST, OrderHelper.STATUS_INCORRECT]) {
                def customerTransaction = new CustomerTransaction()
                customerTransaction.value = order.totalPayablePrice * priceService.getDisplayCurrencyExchangeRate()
                customerTransaction.date = new Date()
                customerTransaction.type = AccountingHelper.TRANSACTION_TYPE_DEPOSIT
                customerTransaction.order = order
                customerTransaction.creator = order.customer
                customerTransaction.save()

                //save withdrawal transaction
                def transaction = new Transaction()
                transaction.value = order.totalPayablePrice * priceService.getDisplayCurrencyExchangeRate()
                transaction.date = new Date()
                transaction.type = AccountingHelper.TRANSACTION_TYPE_DEPOSIT
                transaction.order = order
                transaction.creator = order.customer
                transaction.save()
            }
        }
        def trackingLog = new OrderTrackingLog()
        trackingLog.action = action
        trackingLog.date = new Date()
        trackingLog.order = order
        trackingLog.user = (User) springSecurityService.currentUser
        trackingLog.title = "order.actions.${action}"
        trackingLog.description = "${params.description}\n${description}"
        if (!trackingLog.validate() || !trackingLog.save()) {
            //tracking log save error
            return
        }

        event(topic: 'order_event', data: [id: order.id, status: order.status], namespace: 'browser')

        redirect(action: 'list', params: [status: oldStatus])
    }

    def act_markAsIncorrect() {
        actOnOrder(
                OrderHelper.STATUS_CREATED,
                OrderHelper.STATUS_INCORRECT,
                OrderHelper.ACTION_MARK_AS_INCORRECT,
                "")
    }

    def act_inquiry() {

        def order = Order.get(params.id)
        OrderItem.findAllByOrderAndDeleted(order, false).findAll {
            it.orderCount < 1 || it.productModel.status != 'exists'
        }.each {
            it.deleted = true
            it.save()
        }

//        priceService.updateOrderPrice(order)

        def validityDate = params.ValidityDate
        if (params.ValidityTime) {
            def hours = params.ValidityTime.toString().split(':')[0].toInteger()
            def minutes = params.ValidityTime.toString().split(':')[1].toInteger()
            validityDate.hours = hours
            validityDate.minutes = minutes
        }
        order.paymentTimeout = validityDate
        order.invoiceType = params.invoiceType
        order.serialNumber = Order.createCriteria().get {
            eq('invoiceType', order.invoiceType)
            projections {
                max "serialNumber"
            }
        } as Integer
        if (!order.serialNumber)
            order.serialNumber = 1100
        order.serialNumber++
        order.deliveryPrice = params.deliveryPrice as Double
        order.save()

        priceService.updateOrderPrice(order)

        OrderJob.schedule(validityDate, [orderId: params.id])

        actOnOrder(
                OrderHelper.STATUS_UPDATING,
                OrderHelper.STATUS_INQUIRED,
                OrderHelper.ACTION_INQUIRY,
                message(
                        code: 'act.inquiry.description',
                        args: [rg.formatJalaliDate(date: validityDate, hm: 'true')]))

        def invoice = new ByteArrayOutputStream()
        pdfService.generateInvoice(order, invoice, true)

        mailService.sendMail {
            multipart true
            to order.ownerEmail
            subject message(code: 'emailTemplates.inquiry_result.subject')
            html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                    model: [message: g.render(template: '/messageTemplates/mail/inquiry_result', model: [order: order]).toString()])
            attachBytes "Invoice.pdf", "application/pdf", invoice.toByteArray()
        }

        messageService.sendMessage(
                order.ownerMobile,
                g.render(template: '/messageTemplates/sms/inquiry_result', model: [order: order]).toString())
    }

    def act_markAsNotExist() {
        def order = Order.get(params.id)

        actOnOrder(
                OrderHelper.STATUS_UPDATING,
                OrderHelper.STATUS_NOT_EXIST,
                OrderHelper.ACTION_MARK_AS_NOT_EXIST,
                "")


        if (order.ownerEmail)
            mailService.sendMail {
                to order.ownerEmail
                subject message(code: 'emailTemplates.not_exist.subject')
                html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                        model: [message: g.render(template: '/messageTemplates/mail/not_exist', model: [order: order]).toString()])
            }

        if (order.ownerMobile)
            messageService.sendMessage(
                    order.ownerMobile,
                    g.render(template: '/messageTemplates/sms/not_exist', model: [order: order]).toString())
    }


    def act_approve() {
        actOnOrder(
                OrderHelper.STATUS_CREATED,
                OrderHelper.STATUS_UPDATING,
                OrderHelper.ACTION_APPROVE,
                "")
    }


    def act_re_approve() {
        actOnOrder(
                OrderHelper.STATUS_NOT_EXIST,
                OrderHelper.STATUS_UPDATING,
                OrderHelper.ACTION_APPROVE,
                "")
    }

    def act_approvePayment() {

        def order = Order.get(params.id)
        def paymentAmount = order.usedAccountValue * priceService.getDisplayCurrencyExchangeRate()

        switch (order.paymentType) {
            case 'online':

                def payment = OnlinePayment.findAllByOrder(order)?.sort { -it.id }?.find()
                if (payment) {
                    paymentAmount += payment.amount

                    //add customer transaction
                    def customerTransaction = new CustomerTransaction()
                    customerTransaction.account = payment.account
                    customerTransaction.value = payment.amount
                    customerTransaction.date = new Date()
                    customerTransaction.type = AccountingHelper.TRANSACTION_TYPE_DEPOSIT
                    customerTransaction.order = payment.order
                    customerTransaction.creator = payment.customer
                    customerTransaction.save()

                    //add transaction
                    def transaction = new Transaction()
                    transaction.account = payment.account
                    transaction.value = payment.amount
                    transaction.date = new Date()
                    transaction.type = AccountingHelper.TRANSACTION_TYPE_DEPOSIT
                    transaction.order = payment.order
                    transaction.creator = payment.customer
                    transaction.save()
                }

                break
            case 'bank-receipt':

                def request = PaymentRequest.findAllByOrder(order)?.sort { -it.id }?.find()
                paymentAmount += request.value

                //add customer transaction
                def customerTransaction = new CustomerTransaction()
                customerTransaction.account = request.account
                customerTransaction.value = request.value
                customerTransaction.date = new Date()
                customerTransaction.type = AccountingHelper.TRANSACTION_TYPE_DEPOSIT
                customerTransaction.order = order
                customerTransaction.creator = order.customer
                customerTransaction.save()

                //add transaction
                def transaction = new Transaction()
                transaction.account = request.account
                transaction.value = request.value
                transaction.date = new Date()
                transaction.type = AccountingHelper.TRANSACTION_TYPE_DEPOSIT
                transaction.order = order
                transaction.creator = order.customer
                transaction.save()

                break
            case 'account-value':
                break
            case 'in-place':
                break
        }

        //save withdrawal customer transaction
        def customerTransaction = new CustomerTransaction()
        customerTransaction.value = paymentAmount
        customerTransaction.date = new Date()
        customerTransaction.type = AccountingHelper.TRANSACTION_TYPE_WITHDRAWAL
        customerTransaction.order = order
        customerTransaction.creator = order.customer
        customerTransaction.save()

        //save withdrawal transaction
        def transaction = new Transaction()
        transaction.value = paymentAmount
        transaction.date = new Date()
        transaction.type = AccountingHelper.TRANSACTION_TYPE_WITHDRAWAL
        transaction.order = order
        transaction.creator = order.customer
        transaction.save()

        def bonDiscount = priceService.findDiscounts("Bon", order.totalPrice, order.items.sum {
            it.orderCount
        }) * priceService.getDisplayCurrencyExchangeRate()
        if (bonDiscount) {
            def boncustomerTransaction = new CustomerTransaction()
            boncustomerTransaction.account = request.account
            boncustomerTransaction.value = bonDiscount
            boncustomerTransaction.date = new Date()
            boncustomerTransaction.type = AccountingHelper.TRANSACTION_TYPE_BON
            boncustomerTransaction.order = order
            boncustomerTransaction.creator = order.customer
            boncustomerTransaction.save()

            //add transaction
            def bontransaction = new Transaction()
            bontransaction.account = request.account
            bontransaction.value = bonDiscount
            bontransaction.type = AccountingHelper.TRANSACTION_TYPE_BON
            bontransaction.order = order
            bontransaction.creator = order.customer
            bontransaction.save()
        }
        actOnOrder(
                OrderHelper.STATUS_PAID,
                OrderHelper.STATUS_PAYMENT_APPROVED,
                OrderHelper.ACTION_APPROVE_PAYMENT,
                "")

        if (grailsApplication.config.sendInvoiceWithApprove) {
//            def os = new ByteArrayOutputStream()
//            pdfService.generateInvoice(order, os, true)
            mailService.sendMail {
//                multipart true
                to order.ownerEmail
                subject message(code: 'emailTemplates.approve_payment.subject')
                html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                        model: [message: g.render(template: '/messageTemplates/mail/approve_payment', model: [order: order]).toString()])
//                attachBytes "invoice.pdf", "application/pdf", os.toByteArray()
            }
        } else {
            mailService.sendMail {
                to order.ownerEmail
                subject message(code: 'emailTemplates.approve_payment.subject')
                html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                        model: [message: g.render(template: '/messageTemplates/mail/approve_payment', model: [order: order]).toString()])
            }
        }

        messageService.sendMessage(
                order.ownerMobile,
                g.render(template: '/messageTemplates/sms/approve_payment', model: [order: order]).toString())
    }

    def act_declinePayment() {
        actOnOrder(
                OrderHelper.STATUS_PAID,
                OrderHelper.STATUS_INCOMPLETE,
                OrderHelper.ACTION_DECLINE_PAYMENT,
                "")

        mailService.sendMail {
            to order.ownerEmail
            subject message(code: 'emailTemplates.decline_payment.subject')
            html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                    model: [message: g.render(template: '/messageTemplates/mail/decline_payment', model: [order: order]).toString()])
        }

        messageService.sendMessage(
                order.ownerMobile,
                g.render(template: '/messageTemplates/sms/decline_payment', model: [order: order]).toString())
    }

    def act_transmission() {
        actOnOrder(
                OrderHelper.STATUS_PAYMENT_APPROVED,
                OrderHelper.STATUS_TRANSMITTED,
                OrderHelper.ACTION_TRANSMISSION,
                "")
    }

    def act_delivery() {

        def order = Order.get(params.id)
        order.deliveryTrackingCode = params.deliveryTrackingCode
        order.buyerName = params.buyerName
        order.buyerAmount = params.buyerAmount

        order.save()

        actOnOrder(
                OrderHelper.STATUS_TRANSMITTED,
                OrderHelper.STATUS_DELIVERED,
                OrderHelper.ACTION_DELIVERY,
                order.deliveryTrackingCode && order.deliveryTrackingCode != '' ? message(code: 'order.deliveryTrackingCode') + ": " + order.deliveryTrackingCode : '')

        OrderItem.findAllByOrderAndDeleted(order, false).each {
            def product = Product.get it.productModel.product.id
            product.saleCount = product.saleCount ? product.saleCount + 1 : 0
//            product.isSynchronized = false
            product.save()
        }

        if (order.deliveryTrackingCode && order.deliveryTrackingCode != '') {

            mailService.sendMail {
                to order.ownerEmail
                subject message(code: 'emailTemplates.delivery_with_tracking_code.subject')
                html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                        model: [message: g.render(template: '/messageTemplates/mail/delivery_with_tracking_code', model: [order: order]).toString()])
            }

            messageService.sendMessage(
                    order.ownerMobile,
                    g.render(template: '/messageTemplates/sms/delivery_with_tracking_code', model: [order: order]).toString())
        } else {

            mailService.sendMail {
                to order.ownerEmail
                subject message(code: 'emailTemplates.delivery_without_tracking_code.subject')
                html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                        model: [message: g.render(template: '/messageTemplates/mail/delivery_without_tracking_code', model: [order: order]).toString()])
            }

            messageService.sendMessage(
                    order.ownerMobile,
                    g.render(template: '/messageTemplates/sms/delivery_without_tracking_code', model: [order: order]).toString())
        }
    }

    def printInvoice() {
        def order = Order.get(params.id)
        if (!order)
            order = Order.findByTrackingCode(params.id)
        response.setHeader("Content-Disposition", "attachment; filename=\"Invoice.pdf\"");
        response.setContentType('application/pdf')
        pdfService.generateInvoice(order, response.outputStream, params.boolean('bg') ?: false)
    }
}
