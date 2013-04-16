<%--
  Created by IntelliJ IDEA.
  User: Farzin
  Date: 4/9/13
  Time: 10:43 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="site"/>
    <title></title>
</head>

<body>
<div class="control-panel">
    <h2><g:message code="controlPanel.orders.yourOrders.${status}.label"/></h2>
    <g:if test="${orderList.empty}">
        <div class="info">
        <div><g:message code="list.empty.message"></g:message></div>
        </div>
    </g:if>
    <g:each in="${orderList}" var="order">
    <div class="group">
        <div class="header">
            <img src="../../images/box.png">
            <h3>#${order.id}</h3>
            <div class="comment"><g:message code="order.totalPrice"></g:message>: <g:formatNumber number="${order.items.unitPrice.sum()}" type="number"></g:formatNumber></div>
            <div class="comment"><g:message code="order.${status}.date"></g:message>: <rg:formatJalaliDate date="${order.trackingLogs.sort{-it.id}.first().date}" hm="true"></rg:formatJalaliDate> </div>
        </div>

        <div class="column1 items">
            <h4><g:message code="controlPanel.orders.actions.label"></g:message></h4>
            <ul class="master">
                <g:each in="${suggestedActions}" var="action">
                    <li>
                        <g:link controller="order" action="${action}" params="${['id':order.id]}"><g:message code="controlPanel.orders.actions.${action}.label"></g:message></g:link></li>
                </g:each>
                <g:each in="${actions}" var="action">
                    <li>
                        <g:link controller="order" action="${action}" params="${['id':order.id]}"><g:message code="controlPanel.orders.actions.${action}.label"></g:message></g:link></li>
                </g:each>
            </ul>
        </div>

        <div class="column2 items">
            <h4><g:message code="controlPanel.orders.productList.label"></g:message></h4>
            <ul class="master">
                <g:each in="${order.items}" var="orderItem">
                <li>
                    <g:link controller="site" action="product" params="${[id:orderItem.product.id]}">${orderItem.product}</g:link></li>
                </g:each>
            </ul>
        </div>
    </div>
    </g:each>
</div>

</body>
</html>