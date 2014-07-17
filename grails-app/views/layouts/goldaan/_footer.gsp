<div class="footer-info-container">

    <div class="footer-info">
        <hr/>

        <div>
            <ul class="column">
                <li class="bold">
                    <g:message code="rules"/>
                </li>
                <li>
                    <a href="${createLink(controller: 'site', action: 'shoppingRules')}"><g:message
                            code="rules.shoppingRules"/></a>
                </li>
                <li>
                    <a href="${createLink(controller: 'site', action: 'customerRights')}"><g:message
                            code="rules.customerRights"/></a>
                </li>

            </ul>
        </div>

        <div>
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
            </ul>
        </div>

        <div>
            <ul class="column">
                <li class="bold">
                    <g:message code="footerItems.about"/>
                </li>
                <li>
                    <a href="${createLink(controller: 'site', action: 'aboutUs')}"><g:message
                            code="footerItems.tour"/></a>
                </li>
                <li>
                    <a href="${createLink(controller: 'site', action: 'suppliers')}"><g:message
                            code="help.supplier"/></a>
                </li>
            </ul>
        </div>
    </div>

    <div>
        <hr/>
        <div>
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
        </div>

        <div>
            <ul class="third-party">
                <li>
                    <img src="${resource(dir: 'images/third-party', file: 'best-brands.png')}"/>
                </li>
                <li>
                    <img src="${resource(dir: 'images/third-party', file: 'rights-all.png')}"/>
                </li>
            </ul>
        </div>

    </div>
</div>

<div class="footer">
    <g:message code="copyright-goldaan"/>
</div>
