<%@ page import="eshop.ProductService" %>
<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6" ng-app='eshop' xmlns:ng="http://angularjs.org" id="ng-app"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7" ng-app='eshop' xmlns:ng="http://angularjs.org" id="ng-app"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8" ng-app='eshop' xmlns:ng="http://angularjs.org" id="ng-app"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9" ng-app='eshop' xmlns:ng="http://angularjs.org" id="ng-app"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js" ng-app='eshop'><!--<![endif]-->
<head>
    <title><g:layoutTitle/></title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <canonical:show/>
    <meta name="robots" content="index, follow"/>
    <meta name="google-site-verification" content="mH1bB8PXNP_Qn0W29M_XLwI2aFf5EuHhkPRfTJCEW8M"/>
    <link rel="shortcut icon" href="${resource(dir: 'images/felfel', file: 'favicon.ico')}" type="image/x-icon">
    <!--[if lte IE 8]>
    <script language="javascript" src="${resource(dir: 'js', file: 'ieFix.js')}" type="text/javascript"></script>
    <script language="javascript" src="${resource(dir: 'js', file: 'json3.js')}" type="text/javascript"></script>
    <script>
        document.createElement('ng-include');
        document.createElement('ng-pluralize');
        document.createElement('ng-view');

        // Optionally these for CSS
        document.createElement('ng:include');
        document.createElement('ng:pluralize');
        document.createElement('ng:view');
      </script>
    <![endif]-->
    <g:javascript library="jquery"/>
    %{--<link rel="stylesheet" href="${resource(dir: 'css/templates', file: 'firstPage.css')}" type="text/css">--}%
    %{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'siteUI.css')}" type="text/css">--}%
    <link rel="stylesheet" href="${resource(dir: 'bootstrap/css', file: 'bootstrap.min.css', plugin: 'rapid-grails')}"/>
    <link rel="stylesheet"
          href="${resource(dir: 'bootstrap/css', file: 'bootstrap-responsive.min.css', plugin: 'rapid-grails')}"/>
    %{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-amazon.css')}"/>--}%
    <link rel="stylesheet" href="${resource(dir: 'bootstrap/css', file: 'bootstrap-rtl.css', plugin: 'rapid-grails')}"/>
    %{--<link href="${resource(dir: 'css', file: 'siteUI.css')}" rel="stylesheet" type="text/css"/>--}%
    %{--<link href="${resource(dir: 'css', file: 'site.css')}" rel="stylesheet" type="text/css"/>--}%
    <link href="${resource(dir: 'css', file: 'jquery.rollbar.css')}" rel="stylesheet" type="text/css"/>
    <link href="${resource(dir: 'css', file: 'jquery.msgGrowl.css')}" rel="stylesheet" type="text/css"/>
    <link href="${resource(dir: 'css', file: 'jquery.tipsy.css')}" rel="stylesheet" type="text/css"/>

    <r:layoutResources/>
    <script type="text/javascript"
            src="${resource(plugin: 'rapid-grails', dir: 'js', file: 'angular.min.js')}"></script>
    <script type="text/javascript">
        var basketCounter = ${session.getAttribute("basketCounter") ?: 0};
        var basket = ${(session.getAttribute("basket")?: []) as grails.converters.JSON};
        var compareListCounter = ${session.getAttribute("compareListCounter") ?: 0};
        var compareList = ${(session.getAttribute("compareList")?: []) as grails.converters.JSON};
        <sec:ifLoggedIn>
        <% def priceService = grailsApplication.classLoader.loadClass('eshop.PriceService').newInstance() %>
        <g:set var="wishList" value="${eshop.Customer.findByUsername(sec.username())?.wishList?.collect{[id:it.id, title: it.toString(), price: priceService.calcProductPrice(it.id).showVal]}?:[]}"/>
        var wishListCounter = ${wishList? wishList.count {it}: 0};
        var wishList = ${(wishList?: []) as grails.converters.JSON};
        var wishListEnabled = true;
        </sec:ifLoggedIn>
        <sec:ifNotLoggedIn>
        var wishListCounter = 0;
        var wishList = ${[] as grails.converters.JSON};
        var wishListEnabled = false;
        var wishListNotEnabledMessage = '${message(code:'wishList.notEnabled.message')}';
        </sec:ifNotLoggedIn>
        var contextRoot = "${createLink(uri: '/')}";
        var mainSlides = [];
        var mainSlideSize = 0;
        var specialSaleSlides = [];
        var specialSaleSlideSize = 0;
        var mainSlideSize = 0;
        var mainSlideWidth = 0;
        var mainSlideHeight = 0;
        var specialSaleSlideWidth = 0;
        var specialSaleSlideHeight = 0;
        var specialSaleSlideSize = 0;

        var searchAutoCompleteUrl = '${createLink(controller: 'site', action: 'searchAutoComplete')}';
    </script>
    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.rollbar.js')}"
            type="text/javascript"></script>
    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.mousewheel.js')}"
            type="text/javascript"></script>
    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.msgGrowl.js')}"
            type="text/javascript"></script>
    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.tipsy.js')}" type="text/javascript"></script>
    <script language="javascript"
            src="${resource(dir: "js/${grailsApplication.config.eShop.instance}", file: 'eshopCtrl.js')}"
            type="text/javascript"></script>
    %{--<script language="javascript" src="${resource(dir: 'js', file: 'jquery.tpl_layout1.1.6.min.js')}"--}%
    %{--type="text/javascript"></script>--}%
    <g:layoutHead/>
    <link href="${resource(dir: 'css', file: 'felfel.css')}" rel="stylesheet" type="text/css"/>
    %{--<script language="javascript" src="${resource(dir: 'js', file: 'jquery.watch.js')}" type="text/javascript"></script>--}%
    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.hoverIntent.js')}"
            type="text/javascript"></script>
    %{--<script language="javascript" src="${resource(dir: 'js', file: 'common.js')}" type="text/javascript"></script>--}%
    <script language="javascript" src="${resource(dir: 'js', file: 'search-auto-complete-felfel.js')}"
            type="text/javascript"></script>
    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.maskinput.js')}"
            type="text/javascript"></script>
    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.dotdotdot.js')}"
            type="text/javascript"></script>
    <script language="javascript" src="${resource(dir: 'js', file: 'felfel.js')}" type="text/javascript"></script>
    <link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'jquery.qtip.css')}"/>
    <script type="text/javascript" language="javascript" src="${resource(dir: 'js', file: 'jquery.qtip.js')}"></script>
    <script type="text/javascript" language="javascript" src="${resource(dir: 'js', file: 'imagesloaded.js')}"></script>
    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.raty.js')}" type="text/javascript"></script>
    <script language="JavaScript" type="text/javascript"
            src="${resource(dir: 'js', file: 'jquery.sticky-kit.js')}"></script>
</head>

<body class="main" ng-controller="eshopCtrl">
<div id="main-container"
    <g:if test="${background}">
        style="background-image: url('${createLink(controller: "image", action: "index")}/${background.id}?type=mainSlide')"
    </g:if> >
    %{--<g:render template="/layouts/${grailsApplication.config.eShop.instance}/alexa"/>--}%
    <g:render template="/layouts/${grailsApplication.config.eShop.instance}/header" key="${sec.username()}"/>
    <div id="main">
        <g:layoutBody/>
        <g:render template="/inquired_payments"/>

    </div>
    <g:render template="/layouts/${grailsApplication.config.eShop.instance}/footer"/>
</div>

<r:layoutResources/>
<script src="${resource(dir: 'bootstrap/js', file: 'bootstrap.min.js', plugin: 'rapid-grails')}"></script>
<script language="javascript" src="${resource(dir: 'js', file: 'jquery.lazyLoad.js')}" type="text/javascript"></script>

<!-- Modal -->
<div id="quickViewModal" class="modal hide fade" tabindex="-1" role="window" aria-labelledby="quickViewModalLabel"
     aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    </div>

    <div class="modal-body" style="margin:0 !important;">
    </div>
</div>
<script language="javascript" type="text/javascript">
    function quickViewProduct(id, modelId) {

        $("#quickViewModal .modal-body").html('<img class="loading" src="${resource(dir: 'images', file: 'loading.gif')}"/>');
        $("#quickViewModal").modal({
            show: true
        });
        $("#quickViewModal .modal-body").load('${createLink(controller: 'site', action: 'productQuickView')}/' + id + '?model=' + modelId, function () {
        });
    }
</script>
<g:render template="/layouts/felfel/google_analytics"/>
</body>
</html>
