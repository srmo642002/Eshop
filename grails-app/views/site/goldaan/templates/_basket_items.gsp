<script language="javascript" type="text/javascript">

    $(".shopping-basket").accordion({
        heightStyle: "content"
    });

    function updateDeliveryMethods() {

        <g:if test="${currentStep >= 4}">
        $('#delivery-methods-container').html('<img class="loading" src="${resource(dir: 'images', file: 'loading.gif')}"/> ${message(code: 'waiting')}').load('${createLink(controller: 'basket', action: 'deliveryMethods')}', function () {
        });
        </g:if>
    }

</script>

<div class="shopping-basket">
    <table>
        <tr>
            <th></th>
            <th class="small"><g:message code="orderItem.unitPrice"/>(<eshop:currencyLabel/>)</th>
            <th class="small"><g:message code="count"/></th>
            <th class="small"><g:message code="basket.totalPriceItems"/>(<eshop:currencyLabel/>)</th>
            <th class="small"><g:message code="basket.items.delete"/></th>
        </tr>
        <tr ng-repeat="basketItem in basket" class="basketItem">

            <td>
                <div class="basket-item-info">
                    <span class="image"><img
                            ng-src="{{contextRoot}}image/index/{{basketItem.id}}?type=productModel&wh=100x100"/>
                    </span>
                    <span class="name"><h3><a
                            ng-href="{{contextRoot}}site/product/{{basketItem.productId}}">
                        {{itemFirstLine(basketItem.name)}}
                        <br/><span class="smaller">{{itemSecondLine(basketItem.name)}}</span>
                        <br/><span class="smaller"><g:message code="seller-goldaan"/>: {{itemThirdLine(basketItem.name)}}</span>

                    </a>
                    </h3>
                        <span ng-repeat="addedValueName in basketItem.selectedAddedValueNames" class="addedValue">
                            <span class="plus">+</span> {{addedValueName}}
                        </span>
                        <div ng-repeat="addedValueInstance in basketItem.selectedAddedValueInstances" class="addedValue added-value-instance">
                            <span class="plus">+</span> {{addedValueInstance.title}} <span onclick="removeAddedValue('{{basketItem.id}}','{{addedValueInstance.typeId}}')" class="added-value-remove">x</span>
                            <div class="hidden added-value-qtip">
                                <div class="added-value-qtip-content">
                                    <table>
                                        <tr>
                                            <td>
                                                <div ng-show="addedValueInstance.title">{{addedValueInstance.title}} {{addedValueInstance.subTitle}}</div>
                                                <div ng-show="addedValueInstance.description"><g:message code="addedValue-desc" args="['']" /> {{addedValueInstance.title}}: {{addedValueInstance.description}}</div>
                                                <div ng-show="addedValueInstance.from"><g:message code="addedValue.from" />: {{addedValueInstance.from}}</div>
                                                <div ng-show="addedValueInstance.price"><g:message code="addedValue.price" />: {{addedValueInstance.price}}</div>
                                            </td>
                                            <td>
                                                <img ng-show="addedValueInstance.id" src="<g:createLink controller="image" params="[type:'addedValue']" />&id={{addedValueInstance.id}}" alt="">
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </span>
                </div>
                <div class="added-value-types-select">
                    %{--<g:each in="${addedValueTypes}">--}%
                        <div class="" ng-repeat="addedValueType in basketItem.addedValueTypes">
                            <a class="btn btn-added-value btn-{{btnClasses[$index%4]}}" id="{{basketItem.id}}_{{addedValueType.id}}" href="#" onclick="showAddedValueDialog(this,'{{addedValueType.id}}','{{basketItem.productId}}','{{basketItem.id}}')">
                                <img src="<g:createLink controller="image" params="[type:'addedValueType']"/>&id={{addedValueType.id}}" alt="">
                                <g:message code="adding" /> {{addedValueType.title}}
                                <span ng-show="addedValueType.description">({{addedValueType.description}})</span>
                            </a>
                            %{--<button class="btn btn-info btn-mini">${it.title}</button>--}%
                        </div>
                    %{--</g:each>--}%
                </div>
                
                <div class="clearfix"></div>
            </td>
            <td colspan="4">
                <table>
                    <tr>
                        <td class="small"><span class="price"><b>{{basketItem.realPrice | number:0}}</b> </span></td>
                        <td class="small"><span class="count" ng-show="!basketItem.externalDiscount"><input type="text" value="{{basketItem.count}}" onkeyup="updateBasketItemCount('{{basketItem.id}}', this.value, updateDeliveryMethods)"/></span>
                            <span class="count" ng-show="basketItem.externalDiscount">{{basketItem.count}}</span></td>
                        <td class="small"><span class="price"><b>{{basketItem.realPrice * basketItem.count | number:0}}</b></span></td>
                        <td class="small"><span class="delete"><a type="button" onclick="removeFromBasket('{{basketItem.id}}', updateDeliveryMethods)"><g:img dir="/images/" file="remove-basket-item.png"/></a></span></td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <textarea name="description_{{basketItem.id}}" class="basket-item-description" placeholder="<g:message code="description" />">{{basketItem.description}}</textarea>
                        </td>
                    </tr>
                </table>
            </td>

        </tr>
        <tr>
            <td>
                <div class="delivery-methods">
                    <span><g:message code="deliveryMethod" />:</span>
                    <span>
                        <g:each in="${deliveryMethods}">
                            <div class="delivery-method">
                                <label>
                                    <input ng-model="deliveryMethod" type="radio" value="${it.id}" name="deliveryMethod" onchange="setDeliveryPrice(this, '${it}', ${it.price}, ${it.hidePrice},'${it.name}');">
                                    <img src="${createLink(controller: 'image', params: [id: it.id, type: 'deliveryMethod'])}"/>
                                    ${it.name} (${it.description})
                                </label>
                            </div>
                        </g:each>
                    </span>
                </div>
            </td>
        </tr>
    </table>
    <div class="basket-actions">
        <button class="btn btn-success" onclick="updateDescriptionAndDeliverMethod()"><g:message code="next-step" /></button>
    </div>
</div>