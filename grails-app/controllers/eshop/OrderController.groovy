package eshop

import eshop.accounting.Account

class OrderController {

    def springSecurityService
    def priceService
    def mellatService
    def accountingService

    def create() {

        //save order
        def order = (Order)session["order"]
        order.customer = (Customer) springSecurityService.currentUser
        order.status = OrderHelper.STATUS_CREATED

        def sendingAddress = (Address)session["sendingAddress"]
        sendingAddress.save()

        def billingAddress = (Address)session["billingAddress"]
        billingAddress.save()

        order.sendingAddress = sendingAddress
        order.billingAddress = billingAddress

        if (!order.validate() || !order.save()) {
            //order save error
            return
        }

        //save order tracking log
        def trackingLog = new OrderTrackingLog()
        trackingLog.action = OrderHelper.ACTION_CREATION
        trackingLog.date = new Date()
        trackingLog.order = order
        trackingLog.user = (User) springSecurityService.currentUser
        trackingLog.title = message(code: 'order.trackingLog.action.creation.title', params: [trackingLog.date, trackingLog.user])
        if (!trackingLog.validate() || !trackingLog.save()) {
            //tracking log save error
            return
        }

        def basket = session.getAttribute("basket")
        basket.each() { basketItem ->
            def orderItem = new OrderItem()
            orderItem.product = Product.get(basketItem.id)
            orderItem.order = order
            orderItem.orderCount = basketItem.count
            def price = priceService.calcProductPrice(basketItem.id).mainVal
            orderItem.unitPrice = price ? price : 0
            if (!orderItem.validate() || !orderItem.save()) {
                //order item save error
            }
        }

        session.setAttribute("basket", [])
        session.setAttribute("basketCounter", 0)

        redirect(action: 'payment', params: [id: order.id])
    }

    def payment(){
        [
                accountsForOnlinePayment: Account.findAllByHasOnlinePayment(true),
                accounts: Account.findAll(),
                customerAccoutnValue: accountingService.calculateCustomerAccountValue(springSecurityService.currentUser)
        ]
    }

    def onlinePayment() {
        if (params.bank) {
            def order = Order.get(params.id)
            switch (params.bank) {
                case 'mellat':
                    def result = mellatService.prepareForPayment(order.id, order.items.sum { it.orderCount * it.unitPrice }, order.customerId)
                    if (result[0] == 0)
                        [refId: result[1]]
                    else
                        flash.message = result[0]
                    break
                case 'pasargad':
                    break
            }
        }
    }

    def list() {

        def status = params.status
        def orderList = Order.findAllByStatusAndCustomer(status, springSecurityService.currentUser)
        def actions = []
        def suggestedActions = []

        switch (status) {
            case OrderHelper.STATUS_CREATED:
                suggestedActions = [OrderHelper.ACTION_PAYMENT]
                actions = [OrderHelper.ACTION_CANCELLATION]
                break;
            case OrderHelper.STATUS_PAID:
                suggestedActions = []
                actions = []
                break;
            case OrderHelper.STATUS_TRANSMITTED:
                suggestedActions = []
                actions = []
                break;
            case OrderHelper.STATUS_DELIVERED:
                suggestedActions = []
                actions = []
                break;
            case OrderHelper.STATUS_CANCELLED:
                suggestedActions = [OrderHelper.ACTION_REACTIVATION]
                break;
        }

        render view: '/order/list', model: [
                orderList: orderList.sort { -it.id },
                status: status,
                suggestedActions: suggestedActions,
                actions: actions,
        ]
    }

    def savePaymentRequest(){
        def paymentRequest = new PaymentRequest()
        paymentRequest.value = params.value.toInteger()
        paymentRequest.trackingCode = params.trackingCode
        paymentRequest.creationDate = new Date()
        paymentRequest.owner = (Customer)springSecurityService.currentUser
        paymentRequest.account = Account.get(params.account)
        paymentRequest.order = Order.get(params.order.id)
        paymentRequest.usingCustomerAccountValueAllowed = params.usingCustomerAccountValueAllowed
        if(paymentRequest.validate() && paymentRequest.save()){
            flash.message = message(code:"order.payment.paymentRequest.succeed")
            redirect(action: 'payment', params: [id:params.paymentId])
        }
    }

}
