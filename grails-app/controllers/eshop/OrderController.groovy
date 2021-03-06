package eshop

import eshop.accounting.Account
import eshop.accounting.AccountFilter
import eshop.accounting.CustomerTransaction
import eshop.accounting.OnlinePayment
import eshop.accounting.PaymentRequest
import eshop.accounting.Transaction
import eshop.delivery.DeliverySourceStation
import eshop.discout.ExternalDiscount
import fi.joensuu.joyds1.calendar.JalaliCalendar
import grails.plugins.springsecurity.Secured
import org.omg.CORBA.Environment

class OrderController {

    def springSecurityService
    def priceService
    def mellatService
    def samanService
    def accountingService
    def jmsService
    def deliveryService
    def mailService
    def messageService
    def pdfRenderingService
    def pdfService
    def grailsApplication

    static exposes = ['jms']

//    @grails.plugin.jms.Queue(name='order.new')
    def testEvents() {
        event(topic: 'order_event', data: [id: 41], namespace: 'browser')
//        jmsService.send(topic:'order_event', [id:8])
        render 0
    }

    def list() {

        if (!springSecurityService.loggedIn) {
            redirect(controller: 'login')
            return
        }

        def status = params.status
        def orderList
        if (status)
            orderList = Order.findAllByStatusAndCustomer(status, springSecurityService.currentUser as Customer)
        else
            orderList = Order.findAllByCustomer(springSecurityService.currentUser as Customer)

        def actions = []
        def suggestedActions = []

        if (status == OrderHelper.STATUS_INQUIRED)
            suggestedActions = ['completion']

        render view: "/order/${session.mobile ? 'listMobile' : 'list'}", model: [
                orderList       : orderList.sort { -it.id },
                status          : status,
                suggestedActions: suggestedActions,
                actions         : actions,
        ]
    }

    def track() {
        def customer = springSecurityService.currentUser

        if (!params.trackingCode) {
            render view: session.mobile ? 'trackMobile' : 'track', model: [order: null, customer: customer, search: false]
            return
        }

        def order = Order.findByTrackingCode(params.trackingCode)
        def actions = []
        def suggestedActions = []

        if (!order) {
            render view: session.mobile ? 'trackMobile' : 'track', model: [order: null, customer: customer, search: true]
            return
        }

        if (order.status == OrderHelper.STATUS_INQUIRED)
            suggestedActions = [OrderHelper.ACTION_COMPLETION]

        def payment
        if (order.paymentType == 'online')
            payment = OnlinePayment.findByOrder(order)

        def invoiceTitle = message(code: 'order.preInvoice.title')
        switch (order.status) {
            case OrderHelper.STATUS_PAID:
                invoiceTitle = message(code: 'order.finalInvoice.title')
                break
            case OrderHelper.STATUS_TRANSMITTED:
                invoiceTitle = message(code: 'order.finalInvoice.title')
                break
            case OrderHelper.STATUS_DELIVERED:
                invoiceTitle = message(code: 'order.finalInvoice.title')
                break
        }

        render view: session.mobile ? 'trackMobile' : 'track', model: [
                order           : order,
                customer        : customer,
                actions         : actions,
                suggestedActions: suggestedActions,
                payment         : payment,
                invoiceTitle    : invoiceTitle,
                search          : true
        ]
    }

    def finalizeOrder() {
        def customer = springSecurityService.currentUser as Customer
        Order order = new Order()
        order.ownerName = session['buyerName']
        if (customer) {
            order.ownerEmail = customer.email
            order.ownerMobile = customer.mobile
            order.ownerTelephone = customer.telephone
            order.customer = customer
        } else {
            order.ownerEmail = session['buyerEmail']
            order.ownerMobile = session['buyerPhone']
//            order.ownerTelephone = session['buyerPhone']
        }
        Address address = new Address()
        address.addressLine1 = session['deliveryAddressLine']
        address.telephone = session['deliveryPhone']
        address.city = City.get(session['deliveryCity'])
        address.title = session['deliveryName']
        if (!address.save())
            println(address.errors.allErrors)


        order.sendingAddress = address
        order.billingAddress = address
        order.paymentType = session['paymentType']
        order.sendFactorWith = Boolean.parseBoolean(session['sendFactor'] ?: 'false')


        order.deliveryPrice = (session['deliveryPrice'] ?: params.price ?: '0') as double
        order.callBeforeSend = session['callBeforeSend']
        order.deliverySourceStation = session['deliveryMethod'] ? (eshop.delivery.DeliveryMethod.get(session['deliveryMethod'])?.sourceStations?.find()) : DeliverySourceStation.get(params.deliverySourceStation)
        order.deliveryTime = "${session['deliveryDate_hour']}:${session['deliveryDate_minute']} ${session['deliveryDate']}"
        order.status = OrderHelper.STATUS_CREATED
        order.save()
        if (order.errors.allErrors)
            println order.errors.allErrors

        def cal = Calendar.getInstance()
        cal.setTime(new Date())
        def jc = new JalaliCalendar(cal)
        order.trackingCode = String.format(
                "%02d%02d%02d%01d%03d",
                jc.getYear() % 100,
                jc.getMonth(),
                jc.getDay(),
                0, //customer type flag
                order.id % 1000
        )

        def basket = session.getAttribute("basket")
        basket.each() { basketItem ->
            def orderItem = new OrderItem()
            orderItem.productModel = ProductModel.get(basketItem.id)
            orderItem.order = order
            orderItem.description = basketItem.description
            orderItem.orderCount = basketItem.count
            orderItem.unitPrice = basketItem.realPrice
            if (basketItem.externalDiscount) {
                orderItem.externalDiscount = ExternalDiscount.get(basketItem.externalDiscount)
                orderItem.externalDiscount.purchaseDate = new Date()
                orderItem.externalDiscount.user = customer
                orderItem.externalDiscount.save()
            }
            orderItem.description = basketItem.description
            basketItem.selectedAddedValueInstances?.each {
                orderItem.addToAddedValueInstances(new AddedValueInstance(
                        addedValue: AddedValue.get(it.value.id),
                        description: it.value.description,
                        from: it.value.from,
                        orderCount: (it.value.orderCount ?: '0') as int,
                        image: it.value.image ? new Content(name: it.value.title, contentType: 'image', fileContent: session[it.value.image]) : null
                ))
            }
            orderItem.save()
        }
        if ((session['payFromAccount'] || params.boolean('useBon')) && customer) {
            def acctValue = accountingService.calculateCustomerAccountValue(customer) / priceService.getDisplayCurrencyExchangeRate()
            order.usedAccountValue = acctValue
        }
//        println session['payFromAccount']
        priceService.updateOrderPrice(order);
//        println order.usedAccountValue
        def trackingLog = new OrderTrackingLog()
        trackingLog.action = OrderHelper.ACTION_CREATION
        trackingLog.date = new Date()
        trackingLog.order = order
        trackingLog.user = springSecurityService.currentUser as User
        trackingLog.title = "order.actions.${OrderHelper.ACTION_CREATION}"
        if (!trackingLog.validate() || !trackingLog.save()) {
            //tracking log save error
            return
        }
        event(topic: 'order_event', data: [id: order.id, status: OrderHelper.STATUS_CREATED], namespace: 'browser')
        try {
            mailService.sendMail {
                to order.ownerEmail
                subject message(code: 'emailTemplates.order_created.subject')
                html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                        model: [message: g.render(template: '/messageTemplates/mail/order_created', model: [order: order]).toString()])
            }
        } catch (x) {
            x.printStackTrace()
        }
        def messageText = g.render(template: '/messageTemplates/sms/order_created', model: [order: order]).toString()
        def mobile = order.ownerMobile
        Thread.start {
            messageService.sendMessage(
                    mobile,
                    messageText)
        }
        if (grailsApplication.config.orderCreateNotifiers) {
            def adminText = g.render(template: '/messageTemplates/sms/orderCreatedAdminNotify', model: [order: order.refresh()]).toString()
            def adminNotifiers = grailsApplication.config.orderCreateNotifiers
            Thread.start {
                adminNotifiers.split(',').each {
                    messageService.sendMessage(
                            it,
                            adminText)
                }
            }
        }
        session.setAttribute("basket", [])
        session.setAttribute("basketCounter", 0)
        session.removeAttribute("order")
        session.removeAttribute("sendingAddress")
        session.removeAttribute('buyerName')
        session.removeAttribute('buyerEmail')
        session.removeAttribute('buyerPhone')
        session.removeAttribute('sendFactor')
        session.removeAttribute('deliveryAddressLine')
        session.removeAttribute('deliveryPhone')
        session.removeAttribute('deliveryCity')
        session.removeAttribute('deliveryName')
        session.removeAttribute('paymentType')
        session.removeAttribute('deliveryPrice')
        session.removeAttribute('callBeforeSend')
        session.removeAttribute('deliveryMethod')
        session.removeAttribute('deliveryDate_hour')
        session.removeAttribute('deliveryDate_minute')
        session.removeAttribute('deliveryDate')
        session.removeAttribute('deliveryDate_month')
        session.removeAttribute('deliveryDate_day')
        session.removeAttribute('deliveryDate_hour')
        session.removeAttribute('deliveryDate_minute')
        session.removeAttribute('payFromAccount')
        session['payFromAccount'] = false
        session.removeAttribute("billingAddress")
        session.removeAttribute("checkout_customerInformation")
        session.removeAttribute("checkout_address")
        session.removeAttribute("checkout_customInvoiceInformation")
        session.removeAttribute("forwardUri")
        session.setAttribute("basket", [])
        session.setAttribute("basketCounter", 0)
        session.removeAttribute("order")
        session.removeAttribute("sendingAddress")
        session.removeAttribute("billingAddress")
        session.removeAttribute("checkout_customerInformation")
        session.removeAttribute("checkout_address")
        session.removeAttribute("checkout_customInvoiceInformation")
        session.removeAttribute("forwardUri")
        session.removeAttribute("maxReachedStep")
        if (order.paymentType == 'online' && order.totalPayablePrice > 0) {
//            def customerTransaction = new CustomerTransaction()
//            customerTransaction.value = order.totalPayablePrice * priceService.getDisplayCurrencyExchangeRate()
//            customerTransaction.date = new Date()
//            customerTransaction.type = AccountingHelper.TRANSACTION_TYPE_WITHDRAWAL
//            customerTransaction.order = order
//            customerTransaction.creator = order.customer
//            customerTransaction.save()
//
//            //save withdrawal transaction
//            def transaction = new Transaction()
//            transaction.value = order.totalPayablePrice * priceService.getDisplayCurrencyExchangeRate()
//            transaction.date = new Date()
//            transaction.type = AccountingHelper.TRANSACTION_TYPE_WITHDRAWAL
//            transaction.order = order
//            transaction.creator = order.customer
//            transaction.save()

            redirect(controller: 'order', action: 'remainingPayment', id: order.id)
        } else
            render(view: 'create', model: [trackingCode: order.trackingCode])
    }

    def create() {
        //save order
        def order = session.getAttribute("order") as Order

        order.customer = springSecurityService.currentUser as Customer
        order.status = OrderHelper.STATUS_CREATED

        def sendingAddress = (Address) session["sendingAddress"]
        sendingAddress.save()

        def billingAddress = (Address) session["billingAddress"]
        billingAddress.save()

        order.sendingAddress = sendingAddress
        order.billingAddress = billingAddress

        //set delivery method
        order.deliverySourceStation = DeliverySourceStation.get(params.deliverySourceStation);
        order.deliveryPrice = params.price.toDouble()
        order.optionalInsurance = params["insurance${order.deliverySourceStation?.id}"]
        if (!order.optionalInsurance)
            order.optionalInsurance = false

        order.items?.clear();
        if (!order.validate() || !order.save()) {
            //order save error
            return
        }

        //set tracking code
        def cal = Calendar.getInstance()
        cal.setTime(new Date())
        def jc = new JalaliCalendar(cal)
        order.trackingCode = String.format(
                "%02d%02d%02d%01d%03d",
                jc.getYear() % 100,
                jc.getMonth(),
                jc.getDay(),
                0, //customer type flag
                order.id % 1000
        )

        //save order tracking log
        def trackingLog = new OrderTrackingLog()
        trackingLog.action = OrderHelper.ACTION_CREATION
        trackingLog.date = new Date()
        trackingLog.order = order
        trackingLog.user = springSecurityService.currentUser as User
        trackingLog.title = "order.actions.${OrderHelper.ACTION_CREATION}"
        if (!trackingLog.validate() || !trackingLog.save()) {
            //tracking log save error
            return
        }

        def basket = session.getAttribute("basket")
        basket.each() { basketItem ->
            def orderItem = new OrderItem()
            orderItem.productModel = ProductModel.get(basketItem.id)
            orderItem.order = order
            orderItem.orderCount = basketItem.count

            //added values
            basketItem.selectedAddedValues?.each { addedValue ->
                orderItem.addToAddedValues(AddedValue.get(addedValue.toLong()))
            }

            orderItem.save()
        }

        priceService.updateOrderPrice(order);

        session.setAttribute("basket", [])
        session.setAttribute("basketCounter", 0)
        session.removeAttribute("order")
        session.removeAttribute("sendingAddress")
        session.removeAttribute("billingAddress")
        session.removeAttribute("checkout_customerInformation")
        session.removeAttribute("checkout_address")
        session.removeAttribute("checkout_customInvoiceInformation")
        session.removeAttribute("forwardUri")
        session.removeAttribute("maxReachedStep")

        event(topic: 'order_event', data: [id: order.id, status: OrderHelper.STATUS_CREATED], namespace: 'browser')

        if (grails.util.Environment.current != grails.util.Environment.DEVELOPMENT) {
            mailService.sendMail {
                to order.ownerEmail
                subject message(code: 'emailTemplates.order_created.subject')
                html(view: "/messageTemplates/${grailsApplication.config.eShop.instance}_email_template",
                        model: [message: g.render(template: '/messageTemplates/mail/order_created', model: [order: order]).toString()])
            }

            messageService.sendMessage(
                    order.ownerMobile,
                    g.render(template: '/messageTemplates/sms/order_created', model: [order: order]).toString())
        }

        [trackingCode: order.trackingCode]
//        flash.message = message(code: 'order.creation.success.message')
//        redirect(controller: 'customer', action: 'panel')
    }

    def payment() {
        def order = Order.get(params.id)
        if (order.customer)
            redirect(action: 'registeredPayment', params: params)
        else
            redirect(action: 'notRegisteredPayment', params: params)
    }

    def notRegisteredPayment() {
        def order = Order.get(params.id)
        order.usedAccountValue = 0
        order.totalPayablePrice = order.totalPrice
        order.save()

        redirect(action: 'remainingPayment', params: [id: params.id])
    }

    @Secured([RoleHelper.ROLE_CUSTOMER])
    def registeredPayment() {
        def order = Order.get(params.id)
        def customer = springSecurityService.currentUser as Customer
        if (customer?.id != order.customer?.id) {
            redirect(uri: '/notFound')
            return
        }
        def customerAccountValue = customer ? (accountingService.calculateCustomerAccountValue(customer) / priceService.getDisplayCurrencyExchangeRate()) : 0
        if (customerAccountValue > 0)
            render view: session.mobile ? 'paymentMobile' : 'payment', model: [
                    order               : order,
                    orderPrice          : order.totalPrice,
                    customerAccountValue: customer ? (accountingService.calculateCustomerAccountValue(customer) / priceService.getDisplayCurrencyExchangeRate()) : 0,
                    customer            : customer
            ]
        else
            redirect(action: 'remainingPayment', params: [id: params.id])

    }

    def payFromAccount() {
        def customer = springSecurityService.currentUser as Customer
        def customerAccountValue = customer ? (accountingService.calculateCustomerAccountValue(customer) / priceService.getDisplayCurrencyExchangeRate()) : 0
        def order = Order.get(params.order.id)
        if (params.payFromAccountType == 'whole')
            order.usedAccountValue = order.totalPrice
        else
            order.usedAccountValue = params.payFromAccountAmount.replace(',', '').toInteger()
        if (order.usedAccountValue > customerAccountValue) {
            flash.message = message(code: 'order.payment.payFromAccountAmount.moreThanCustomerAccount.validator')
            redirect(action: 'payment', params: [id: params.order.id])
            return
        }
        order.totalPayablePrice = order.totalPrice - order.usedAccountValue
        order.save()

        if (order.totalPayablePrice == 0)
            redirect(action: 'payOrderFromAccount', params: [id: params.order.id])
        else
            redirect(action: 'remainingPayment', params: [id: params.order.id])
    }

    def remainingPayment() {
        def order = Order.get(params.id)

        def accountsForOnlinePayment = new ArrayList<Account>()
        OrderItem.findAllByOrderAndDeleted(order, false).each { orderItem ->
            def productType = orderItem.productModel.product.productTypes.toArray().first() as ProductType
            while (productType) {
                AccountFilter.findAllByProductType(productType).each { accountFilter ->
                    if (accountFilter.account.type == 'real' &&
                            accountFilter.account.hasOnlinePayment)
                        if (!accountsForOnlinePayment.any { account ->
                            account.id == accountFilter.account.id
                        } && (!accountFilter.brands
                                || accountFilter?.brands?.isEmpty()
                                || accountFilter?.brands?.any { it.id == orderItem?.productModel?.product?.brand?.id })
                                && accountFilter?.account?.hasOnlinePayment)
                            accountsForOnlinePayment.add(accountFilter.account)
                }

                productType = productType.parentProduct
            }
        }

        accountsForOnlinePayment.addAll(
                Account.findAllByBankNameNotInListAndTypeAndHasOnlinePayment(accountsForOnlinePayment.collect {
                    it.bankName
                } ?: [''], 'legal', true))

        accountsForOnlinePayment.addAll(
                Account.findAllByBankNameNotInListAndHasOnlinePayment(accountsForOnlinePayment.collect {
                    it.bankName
                } ?: [''], true))

        accountsForOnlinePayment.unique { it.bankName }

        def customer = springSecurityService.currentUser as Customer

        render view: session.mobile ? 'remainingPaymentMobile' : 'remainingPayment', model: [
                order                   : order,
                orderPrice              : order.totalPayablePrice,
                accountsForOnlinePayment: accountsForOnlinePayment,
                accounts                : Account.findAllByType('legal'),
                customerAccountValue    : customer ? accountingService.calculateCustomerAccountValue(customer) : 0,
                customer                : customer
        ]
    }

    def onlinePayment() {
        if (params.accountId) {
            def order = Order.get(params.order.id)
            def account = Account.get(params.accountId)
            def model = [bankName: account.bankName]

            def onlinePayment = new OnlinePayment()
            onlinePayment.account = account
            onlinePayment.amount = (params["value"].toInteger() * priceService.getDisplayCurrencyExchangeRate())
            onlinePayment.customer = order.customer
            onlinePayment.date = new Date()
            onlinePayment.order = order
            onlinePayment.usingCustomerAccountValueAllowed = params.usingCustomerAccountValueAllowed ?: order.customer ? true : false
            onlinePayment.save()

            switch (account.bankName) {
                case 'mellat':

                    def result = mellatService.prepareForPayment(account, onlinePayment.id, onlinePayment.amount, order.customerId)
                    if (result[0] == "0")
                        model.refId = result[1]
                    else
                        flash.message = result[0]

                    onlinePayment.initialResultCode = result[0] ?: null
                    if (result.size() > 1)
                        onlinePayment.referenceId = result[1]
                    onlinePayment.save()

                    break
                case 'ogone':
                    model.currency = 'USD'
                    model.amount = onlinePayment.amount / (Currency.findByCode(model.currency)?.exchangeRate ?: 1)
                    model.reservationNumber = onlinePayment.id
                    def onlinePaymentConfiguration = new XmlParser().parseText(onlinePayment.account.onlinePaymentConfiguration)
                    model.merchantId = onlinePaymentConfiguration.userName.text()
                    model.shaPassword = onlinePaymentConfiguration.shaPassword.text()
                    model.customerName = order.ownerName
                    model.customerEmail = order.ownerEmail
                    model.productTitle = order.items.collect {
                        "${it.productModel.product?.manualTitle ? it.productModel.product?.pageTitle : it.productModel.product?.title} ${it.productModel.name} ${it.productModel.product?.brand?.name ?: ""}"
                    }.join(', ')
                    break
                case 'saman':

                    model.amount = onlinePayment.amount
                    model.reservationNumber = onlinePayment.id
                    def onlinePaymentConfiguration = new XmlParser().parseText(onlinePayment.account.onlinePaymentConfiguration)
                    model.merchantId = onlinePaymentConfiguration.userName.text()
                    break
                case 'meb':

                    model.amount = onlinePayment.amount
                    model.reservationNumber = onlinePayment.id
                    def onlinePaymentConfiguration = new XmlParser().parseText(onlinePayment.account.onlinePaymentConfiguration)
                    model.merchantId = onlinePaymentConfiguration.userName.text()
                    break
            }

            order.paymentType = 'online'
            order.save()

            render view: session.mobile ? 'onlinePaymentMobile' : 'onlinePayment', model: model
        }
    }

    def onlinePaymentResultMellat() {

        def model = [:]
        def onlinePayment = OnlinePayment.findByReferenceId(params.RefId.toString())
        if (onlinePayment) {
            onlinePayment.resultCode = params.ResCode.toString()
            onlinePayment.transactionReferenceCode = params.SaleReferenceId.toString()
            onlinePayment.save()
            model = [onlinePayment: onlinePayment]
            if (onlinePayment.resultCode == "0") {
                def verificationResult = mellatService.verifyPayment(onlinePayment.account, onlinePayment.order.id, params.SaleOrderId, onlinePayment.transactionReferenceCode)
                onlinePayment.resultCode = "${onlinePayment.resultCode}-${verificationResult}"
                onlinePayment.save()
                model.verificationResult = verificationResult
                if (verificationResult == "0") {
                    def settleResult = mellatService.settlePayment(onlinePayment.account, onlinePayment.order.id, params.SaleOrderId, onlinePayment.transactionReferenceCode)
                    onlinePayment.resultCode = "${onlinePayment.resultCode}-${settleResult}"
                    onlinePayment.save()
                    if (settleResult == "0" || settleResult == "45")
                        payOrder(onlinePayment, model)
                }
            }
        }

        render view: session.mobile ? 'onlinePaymentResultMobile' : 'onlinePaymentResult', model: model
    }

    def onlinePaymentResultOgone() {
        println params
        def model = [:]
        def reservationNumber = params.orderID?.toLong();
        def status = params.STATUS.toString();
        def referenceNumber = params.PAYID ? params.PAYID.toString() : '';
        model.appURL = params.appURL
        def onlinePayment = OnlinePayment.get(reservationNumber)
        model.onlinePayment = onlinePayment


        onlinePayment.resultCode = status
        onlinePayment.transactionReferenceCode = referenceNumber
        onlinePayment.save()
        model.verificationResult = params.id
        if (params.id == 'accept') {
            model.verificationResult = 0
            payOrder(onlinePayment, model)
        }

        render view: session.mobile ? 'onlinePaymentResultMobile' : 'onlinePaymentResult', model: model
    }

    def onlinePaymentResultSaman() {

        def model = [:]
        def reservationNumber = params.ResNum?.toLong();
        def status = params.State.toString();
        def referenceNumber = params.RefNum ? params.RefNum.toString() : '';
        model.appURL = params.appURL
        def onlinePayment = OnlinePayment.get(reservationNumber)
        model.onlinePayment = onlinePayment

        double state = -100;
        if (status.equals("OK")) {
            state = samanService.verifyPayment(onlinePayment.account, referenceNumber)
        }
        model.verificationResult = state.toInteger()
        if (state.toInteger() > 0)
            onlinePayment.amount = state.toInteger()
        onlinePayment.resultCode = state.toString()
        onlinePayment.transactionReferenceCode = referenceNumber //params.MID
        onlinePayment.save()

        if (state.toInteger() > 0) {
            model.verificationResult = 0
            payOrder(onlinePayment, model)
        }

        render view: session.mobile ? 'onlinePaymentResultMobile' : 'onlinePaymentResult', model: model
    }
    def onlinePaymentResultMeb() {

        def model = [:]
        def reservationNumber = params.ResNum?.toLong();
        def status = params.State.toString();
        def referenceNumber = params.RefNum ? params.RefNum.toString() : '';
        model.appURL = params.appURL
        def onlinePayment = OnlinePayment.get(reservationNumber)
        model.onlinePayment = onlinePayment

        double state = -100;
        if (status.equals("OK")) {
            state = samanService.verifyPayment(onlinePayment.account, referenceNumber)
        }
        model.verificationResult = state.toInteger()
        if (state.toInteger() > 0)
            onlinePayment.amount = state.toInteger()
        onlinePayment.resultCode = state.toString()
        onlinePayment.transactionReferenceCode = referenceNumber //params.MID
        onlinePayment.save()

        if (state.toInteger() > 0) {
            model.verificationResult = 0
            payOrder(onlinePayment, model)
        }

        render view: session.mobile ? 'onlinePaymentResultMobile' : 'onlinePaymentResult', model: model
    }

    def payOrder(OnlinePayment payment, model) {
        //pay order
        if (payment.order) {

            def orderPrice = payment.order.totalPayablePrice
//            def customerAccount = payment.order.usedAccountValue // payment.customer ? accountingService.calculateCustomerAccountValue(payment.customer) : 0
            def payableAmount = (payment.amount / priceService.getDisplayCurrencyExchangeRate())

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

            if (payableAmount >= orderPrice) {

                //set order status
                if (!grailsApplication.config.payOnCheckout)
                    payment.order.status = OrderHelper.STATUS_PAID
                payment.order.paymentDone = true
                payment.order.paymentType = 'online'
                payment.order.save()

                //add customer transaction
                customerTransaction = new CustomerTransaction()
                customerTransaction.value = (payment.order.totalPrice * priceService.getDisplayCurrencyExchangeRate())
                customerTransaction.date = new Date()
                customerTransaction.type = AccountingHelper.TRANSACTION_TYPE_WITHDRAWAL
                customerTransaction.order = payment.order
                customerTransaction.creator = payment.customer
                customerTransaction.save()

                //add transaction
                transaction = new Transaction()
                transaction.value = (payment.order.totalPrice * priceService.getDisplayCurrencyExchangeRate())
                transaction.date = new Date()
                transaction.type = AccountingHelper.TRANSACTION_TYPE_WITHDRAWAL
                transaction.order = payment.order
                transaction.creator = payment.customer
                transaction.save()


                event(topic: 'order_event', data: [id: payment.order.id, status: OrderHelper.STATUS_PAID], namespace: 'browser')

                //save order tracking log
                def trackingLog = new OrderTrackingLog()
                trackingLog.action = OrderHelper.ACTION_COMPLETION
                trackingLog.date = new Date()
                trackingLog.order = payment.order
                trackingLog.user = payment.customer
                trackingLog.title = "order.actions.${OrderHelper.ACTION_COMPLETION}"
                trackingLog.description = """
                ${message(code: 'payment.type')}: ${message(code: 'payment.types.online')}
                ${message(code: 'order.usedAccountValue')}: ${
                    formatNumber(number: payment.order.usedAccountValue / priceService.getDisplayCurrencyExchangeRate(), type: 'number')
                } ${eshop.currencyLabel()}
                ${message(code: 'order.payment.bank')}: ${
                    message(code: "account.${payment.account.bankName}.${payment.account.type}")
                }
                ${message(code: 'order.payment.value')}: ${
                    formatNumber(number: payment.amount / priceService.getDisplayCurrencyExchangeRate(), type: 'number')
                } ${eshop.currencyLabel()}
                ${message(code: 'onlinePayment.transactionReferenceCode')}: ${payment.transactionReferenceCode}
"""
                if (!trackingLog.validate() || !trackingLog.save()) {
                    //tracking log save error
                    return
                }

                //send alert to customer
                mailService.sendMail {
                    to payment.customer ? payment.customer.email : payment.order.ownerEmail
                    subject message(code: 'order.paid.subject')
                    html(view: "/messageTemplates/mail/orderPaid",
                            model: [customerName: payment.customer ? payment.customer.toString() : payment.order.ownerName, order: payment.order])
                }

                if (payment.customer ? payment.customer.mobile : payment.order.ownerMobile)
                    messageService.sendMessage(
                            payment.customer ? payment.customer.mobile : payment.order.ownerMobile,
                            g.render(
                                    template: '/messageTemplates/sms/orderPaid',
                                    model: [customerName: payment.customer ? payment.customer.toString() : payment.order.ownerName, order: payment.order]).toString())

                model.orderPaid = true
            } else {

                //send alert to customer
                mailService.sendMail {
                    to payment.customer ? payment.customer.email : payment.order.ownerEmail
                    subject message(code: 'order.notPaid.subject')
                    html(view: "/messageTemplates/mail/orderNotPaid",
                            model: [customerName: payment.customer ? payment.customer.toString() : payment.order.ownerName, order: payment.order])
                }

                if (payment.customer ? payment.customer.mobile : payment.order.ownerMobile)
                    messageService.sendMessage(
                            payment.customer ? payment.customer.mobile : payment.order.ownerMobile,
                            g.render(
                                    template: '/messageTemplates/sms/orderNotPaid',
                                    model: [customerName: payment.customer ? payment.customer.toString() : payment.order.ownerName, order: payment.order]).toString())

                model.orderPaid = false
            }
        }
    }

    def savePaymentRequest() {

        def order = Order.get(params.order.id)

        def paymentRequest = new PaymentRequest()
        paymentRequest.value = params.value.toInteger()
        paymentRequest.trackingCode = params.trackingCode
        paymentRequest.creationDate = params.date
        paymentRequest.owner = springSecurityService.currentUser as Customer
        paymentRequest.account = Account.get(params.account)
        paymentRequest.order = order
        paymentRequest.usingCustomerAccountValueAllowed = params.usingCustomerAccountValueAllowed ?: false

        def orderPrice = order.totalPayablePrice
//        def customerAccount = order.usedAccountValue // payment.customer ? accountingService.calculateCustomerAccountValue(payment.customer) : 0
        def payableAmount = paymentRequest.value // + customerAccount

        if (payableAmount >= orderPrice) {

            paymentRequest.save()

            order.status = OrderHelper.STATUS_PAID
            order.paymentType = 'bank-receipt'
            order.save()

            event(topic: 'order_event', data: [id: order.id, status: OrderHelper.STATUS_PAID], namespace: 'browser')

            //save order tracking log
            def trackingLog = new OrderTrackingLog()
            trackingLog.action = OrderHelper.ACTION_COMPLETION
            trackingLog.date = new Date()
            trackingLog.order = order
            trackingLog.user = order.customer
            trackingLog.title = "order.actions.${OrderHelper.ACTION_COMPLETION}"
            trackingLog.description = """
                ${message(code: 'payment.type')}: ${message(code: 'payment.types.bankReceipt')}
                ${message(code: 'order.usedAccountValue')}: ${
                formatNumber(number: order.usedAccountValue / priceService.getDisplayCurrencyExchangeRate(), type: 'number')
            } ${eshop.currencyLabel()}
                ${message(code: 'order.payment.bank')}: ${
                message(code: "account.${paymentRequest.account.bankName}.${paymentRequest.account.type}")
            }
                ${message(code: 'order.payment.value')}: ${
                formatNumber(number: paymentRequest.value / priceService.getDisplayCurrencyExchangeRate(), type: 'number')
            } ${eshop.currencyLabel()}
                ${message(code: 'order.payment.trackingCode')}: ${paymentRequest.trackingCode}
                ${message(code: 'order.payment.date')}: ${rg.formatJalaliDate(date: paymentRequest.creationDate)}
"""
            trackingLog.save()

            flash.message = message(code: "order.payment.paymentRequest.succeed")
            redirect(controller: 'customer', action: 'panel', params: [id: params.order.id])
        } else {

            order.save()
            flash.message = message(code: "order.payment.paymentRequest.notEnough")
            redirect(action: 'remainingPayment', params: [id: params.order.id])
        }
    }

    def payOrderFromAccount() {

        def order = Order.get(params.id)

        //set order status
        order.status = OrderHelper.STATUS_PAID
        order.paymentType = 'account-value'
        order.save()

        //add customer transaction
        def customerTransaction = new CustomerTransaction()
        customerTransaction.value = (order.totalPrice * priceService.getDisplayCurrencyExchangeRate())
        customerTransaction.date = new Date()
        customerTransaction.type = AccountingHelper.TRANSACTION_TYPE_WITHDRAWAL
        customerTransaction.order = order
        customerTransaction.creator = springSecurityService.currentUser as User
        customerTransaction.save()

        //add transaction
        def transaction = new Transaction()
        transaction.value = (order.totalPrice * priceService.getDisplayCurrencyExchangeRate())
        transaction.date = new Date()
        transaction.type = AccountingHelper.TRANSACTION_TYPE_WITHDRAWAL
        transaction.order = order
        transaction.creator = springSecurityService.currentUser as User
        transaction.save()

        event(topic: 'order_event', data: [id: order.id, status: OrderHelper.STATUS_PAID], namespace: 'browser')

//        save order tracking log
        def trackingLog = new OrderTrackingLog()
        trackingLog.action = OrderHelper.ACTION_COMPLETION
        trackingLog.date = new Date()
        trackingLog.order = order
        trackingLog.user = order.customer
        trackingLog.title = "order.actions.${OrderHelper.ACTION_COMPLETION}"
        trackingLog.description = """
            ${message(code: 'payment.type')}: ${message(code: 'payment.types.account')}
            ${message(code: 'order.usedAccountValue')}: ${
            formatNumber(number: order.usedAccountValue / priceService.getDisplayCurrencyExchangeRate(), type: 'number')
        } ${eshop.currencyLabel()}
"""
        trackingLog.save()

        flash.message = message(code: 'order.payment.completed')
        redirect(controller: 'customer', action: 'panel')

    }

    def payInPlace() {
        def order = Order.get(params.order.id)
        order.status = OrderHelper.STATUS_PAID
        order.paymentType = 'in-place'
        order.save()

        event(topic: 'order_event', data: [id: order.id, status: OrderHelper.STATUS_PAID], namespace: 'browser')

//        save order tracking log
        def trackingLog = new OrderTrackingLog()
        trackingLog.action = OrderHelper.ACTION_COMPLETION
        trackingLog.date = new Date()
        trackingLog.order = order
        trackingLog.user = order.customer
        trackingLog.title = "order.actions.${OrderHelper.ACTION_COMPLETION}"
        trackingLog.description = """
            ${message(code: 'payment.type')}: ${message(code: 'payment.types.payInPlace')}
            ${message(code: 'order.usedAccountValue')}: ${
            formatNumber(number: order.usedAccountValue / priceService.getDisplayCurrencyExchangeRate(), type: 'number')
        } ${eshop.currencyLabel()}
"""
        trackingLog.save()

        flash.message = message(code: 'order.payment.inPlace.completed')
        redirect(controller: 'customer', action: 'panel')
    }

    def completion() {
        def order = Order.get(params.id)
        if (order.customer) {
            def customer = springSecurityService.currentUser as Customer
            if (customer?.id != order.customer?.id) {
                redirect(uri: '/notFound')
                return
            }
        }
        render view: session.mobile ? 'completionMobile' : 'completion', model: [order: order]
    }

    //<editor-fold desc="invoice">
    def invoice() {
        def order = Order.get(params.id)
        if (order.customer) {
            def customer = springSecurityService.currentUser as Customer
            if (customer?.id != order.customer?.id) {
                redirect(uri: '/notFound')
                return
            }
        }
        def title = message(code: 'order.preInvoice.title')
        switch (order.status) {
            case OrderHelper.STATUS_PAID:
                title = message(code: 'order.finalInvoice.title')
                break
            case OrderHelper.STATUS_TRANSMITTED:
                title = message(code: 'order.finalInvoice.title')
                break
            case OrderHelper.STATUS_DELIVERED:
                title = message(code: 'order.finalInvoice.title')
                break
        }
        render template: 'invoice', model: [order: order, title: title]
    }

    def pdf() {
        def order = Order.get(params.id)
        if (order.customer) {
            def customer = springSecurityService.currentUser as Customer
            if (customer?.id != order.customer?.id) {
                redirect(uri: '/notFound')
                return
            }
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"Invoice.pdf\"");
        pdfService.generateInvoice(order, response.outputStream, true)
    }

    def pdf_html() {
        def order = Order.get(params.id)
        render template: "/order/invoice/print", model: [order: order]
    }

    //</editor-fold>
}
