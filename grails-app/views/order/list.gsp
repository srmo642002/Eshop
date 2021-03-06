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
    <title><g:message code="orders"/> ${status ? message(code:"order.status.${status}") : ''}</title>
</head>

<body>
<div class="control-panel">
    <h2><g:message code="orders"/> ${status ? message(code:"order.status.${status}") : ''}</h2>
    <g:if test="${orderList.empty}">
        <div class="info">
            <div><g:message code="list.empty.message"></g:message></div>
        </div>
    </g:if>
    <g:each in="${orderList}" var="order">
        <div class="group">
            <div class="groupHeader">
                <img src="../../images/box.png">

                <h3>${order.trackingCode ?: '#' + order.id}</h3>
                <g:if test="${!status}">
                    <div class="comment"><g:message code="order.status"></g:message>: <g:message code="order.status.${order.status}"/></div>
                </g:if>
                <div class="comment"><g:message code="order.totalPrice"></g:message>: <g:formatNumber
                        number="${order.totalPrice}"
                        type="number"></g:formatNumber></div>

                <div class="comment"><g:message code="order.${order.status}.date"></g:message>: <rg:formatJalaliDate
                        date="${order.trackingLogs.sort { -it.id }.first().date}" hm="true"></rg:formatJalaliDate></div>

            </div>

            <div class="column1 items">
                <h4><g:message code="controlPanel.orders.actions.label"></g:message></h4>
                <ul class="master">
                    <g:each in="${suggestedActions}" var="action">
                        <li>
                            <g:link class="btn btn-success" controller="order" action="${action}" params="${['id': order.id]}"><g:message
                                    code="controlPanel.orders.actions.${action}.label"></g:message></g:link></li>
                    </g:each>
                    <g:each in="${actions}" var="action">
                        <li>
                            <g:link class="btn btn-warning" controller="order" action="${action}" params="${['id': order.id]}"><g:message
                                    code="controlPanel.orders.actions.${action}.label"></g:message></g:link></li>
                    </g:each>
                </ul>
            </div>

            <div class="column2 items">
                <h4><g:message code="controlPanel.orders.productList.label"></g:message></h4>
                <ul class="master">
                    <g:each in="${order.items.findAll {!it.deleted}}" var="orderItem">
                        <g:if test="${orderItem.productModel}">
                            <li>
                                <g:link controller="site" action="product"
                                        params="${[id: orderItem.productModel?.product?.id]}">${orderItem.productModel}</g:link>
                                <g:if test="${orderItem.productModel.status != 'exists'}">
                                    <span class="comment">(<g:message
                                            code="productModel.status.${orderItem.productModel.status}"></g:message>)</span>
                                </g:if>

                            </li>
                        </g:if>
                    </g:each>
                </ul>
            </div>

            <div class="clearfix"></div>
        </div>
    </g:each>
</div>

</body>
</html>