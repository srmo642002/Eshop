<div class="footer1">

</div>

<div class="footer2">
    %{--<a href="${createLink(uri: '/')}"><g:message code="home.label"/></a> |--}%
    %{--<a href="#"><g:message code="help.label"/></a> |--}%
    %{--<a href="${createLink(uri: '/termsAndConditions')}"><g:message code="rules.label"/></a> |--}%
    %{--<a href="${createLink(uri: '/contactUs')}"><g:message code="contact.label"/></a>--}%
</div>

<div class="footer3">
    <table>
        <tr>
            <td colspan="4">
                <ul class="third-party">
                    <li>
                        <img src="${resource(dir: 'images/third-party', file: 'bonyad-koodak.png')}"/>
                    </li>
                    <li>
                        <img src="${resource(dir: 'images/third-party', file: 'bimeh-asia.png')}"/>
                    </li>
                    <li>
                        <img src="${resource(dir: 'images/third-party', file: 'bank-mellat.png')}"/>
                    </li>
                    <li>
                        <img src="${resource(dir: 'images/third-party', file: 'bank-saman.png')}"/>
                    </li>
                    <li>
                        <img src="${resource(dir: 'images/third-party', file: 'post-iran.png')}"/>
                    </li>
                    <li>
                        <img src="${resource(dir: 'images/third-party', file: 'post-aramex.png')}"/>
                    </li>
                    <li>
                        <img src="${resource(dir: 'images/third-party', file: 'post-tipax.png')}"/>
                    </li>
                </ul>
            </td>
        </tr>
        <tr>
            <td colspan="4">
                <ul class="third-party">
                    <li>
                        <img src="${resource(dir: 'images/third-party', file: 'best-brands.png')}"/>
                    </li>
                    <li>
                        <img src="${resource(dir: 'images/third-party', file: 'rights-all.png')}"/>
                    </li>
                </ul>
            </td>
        </tr>
        <tr>
            <td colspan="4">
                <hr/>
            </td>
        </tr>
        <tr>
            <td>
                <ul class="column">
                    <li class="bold">
                        <g:message code="rules"/>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'shoppingRules')}"><g:message code="rules.shoppingRules"/></a>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'customerRights')}"><g:message code="rules.customerRights"/></a>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'moneyBackConditions')}"><g:message
                                code="footerItems.guarantee"/></a>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'guarantee')}"><g:message
                                code="footerItems.returnRules"/></a>
                    </li>
                </ul>
            </td>
            <td>
                <ul class="column">
                    <li class="bold">
                        <g:message code="help.label"/>
                    </li>
                    <li>
                        <a href="#"><g:message
                                code="help.shopping"/></a>
                    </li>
                    <li>
                        <a href="#"><g:message
                                code="help.register"/></a>
                    </li>
                    <li>
                        <a href="#"><g:message
                                code="help.search"/></a>
                    </li>
                    <li>
                        <a href="#"><g:message
                                code="help.compare"/></a>
                    </li>
                </ul>
            </td>
            <td>
                <ul class="column">
                    <li class="bold">
                        <g:message
                                code="help.all"/>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'shoppingSteps')}"><g:message
                                code="help.shoppingSteps"/></a>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'paymentMethods')}"><g:message
                                code="help.payment"/></a>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'paymentAndDelivery')}"><g:message
                                code="help.paymentAndDelivery"/></a>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'deliveryTips')}"><g:message
                                code="help.deliveryTips"/></a>
                    </li>
                </ul>
            </td>
            <td>
                <ul class="column">
                    <li class="bold">
                        <g:message code="footerItems.about"/>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'aboutUs')}"><g:message
                                code="footerItems.tour"/></a>
                    </li>
                    <li>
                        <a href="http://www.cv.zanbil.ir/"><g:message
                                code="help.employment"/></a>
                    </li>
                    <li>
                        <a href="${createLink(controller: 'site', action: 'suppliers')}"><g:message
                                code="help.supplier"/></a>
                    </li>
                </ul>
            </td>
        </tr>
    </table>
</div>

<div class="footer4">
    <g:render template="/layouts/zanbil/internalLinks"/>
    <g:message code="copyright"/>
</div>
<div id="alexaGoogleAnalitic">

</div>
<script>
    $(function(){
        setTimeout(function(){
            $('#alexaGoogleAnalitic').load('<g:createLink controller="site" action="alexaGooleAnalitic" />')
        },1000);
    })
</script>