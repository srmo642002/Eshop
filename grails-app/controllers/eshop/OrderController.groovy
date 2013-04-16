package eshop

class OrderController {

    def springSecurityService
    def priceService

    def create() {

        //save order
        def order = new Order()
        order.customer = (Customer) springSecurityService.currentUser
        order.status = OrderHelper.STATUS_CREATED
        order.sendingAddress = order.customer.sendingAddress
        order.billingAddress = order.customer.billingAddress
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

        redirect(action: 'payment', params:[id:order.id])
    }

    def payment() {

    }

    def list() {

        def status = params.status
        def orderList = Order.findAllByStatusAndCustomer(status, springSecurityService.currentUser)
        def actions = []
        def suggestedActions = []

        switch (status){
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

}