<%@ page import="grails.converters.JSON; eshop.MenuConfig; eshop.ProductType" %>
<% def productService = grailsApplication.classLoader.loadClass('eshop.ProductService').newInstance() %>
<g:set var="rootProductTypes" value="${productService.findRootProductTypes()}"/>
<g:set var="mainPage" value=""/>
<g:if test="${controllerName == 'site' && actionName == 'index'}">
    <g:set var="mainPage" value="main-page"/>
</g:if>
<div class="wide_container wc_${mainPage}">
    <div class="" id="st_mega_menu_container">
        <div class="container">

            <g:if test="${controllerName == 'site' && actionName == 'index'}">

                <div class="row">
                    <div class="main-page-header">
                        <a style="margin-right:40px;" href="<g:createLink uri="/"/>">
                            <img src="${resource(dir: 'images/goldaan', file: 'logo.png')}" class="logo">
                            %{--<span class="test-version"><g:message code="test-ver"/></span>--}%
                        </a>
                        <h4><g:message code="slogan"/></h4>

                        <div class="header-top-left">
                            <div class="order-tracking-container">

                                <div class="orderTracking">
                                    <g:form method="post" controller="order" action="track">
                                        <div class="input-append">
                                            <g:textField name="trackingCode" id="trackingCodePanel"
                                                         placeholder="${message(code: "order.tracking.goldaan")}"/>
                                            <g:submitButton name="submit" class="btn btn-primary"
                                                            value="${message(code: 'order.tracking.button')}"/>
                                        </div>
                                    </g:form>
                                </div>
                                <script type="text/javascript" language="javascript">
                                    //                                $('#trackingCodePanel').maskInput('9999999999');
                                </script>
                            </div>

                            <div class="contact-phone-number">
                                <g:message code="contact-phone"/>
                            </div>
                        </div>

                    </div>
                    %{--<div class="">--}%
                        %{--<ehcache:render template="banners/enamad"/>--}%
                    %{--</div>--}%
                </div>
            </g:if>
            <div class="row">
                <div class="col-xs-1 text-right pull-right ${mainPage}" id="logo_wrapper">
                    <div id="header_logo_inner">
                        <a href="<g:createLink uri="/"/>" id="header_logo">
                            <img src="${resource(dir: 'images/goldaan', file: 'logo.png')}" class="logo">
                        </a>
                    </div>
                </div>

                <div class="col-xs-7 pull-right">
                    %{--<nav class="clearBoth" role="navigation" id="st_mega_menu_wrap">--}%
                        %{--<ul class="visible-md visible-lg clearfix" id="st_mega_menu">--}%
                        %{--<li class="sttlevel0  m_alignment_0 " id="st_menu_0">--}%
                        %{--<a title="" class="ma_level_0 is_icon_menu " style=""--}%
                        %{--href="#">--}%
                        %{--<i class="icon-home"></i>--}%
                        %{--</a>--}%
                        %{--</li>--}%
                        %{--<ul class="btn-group pull-right">--}%
                            %{--<g:each in="${rootProductTypes}" var="rpt">--}%
                                %{--<g:set var="pt" value="${ProductType.get(rpt.id)}"/>--}%
                                %{--<li class="sttlevel0  m_alignment_0" id="st_menu_${pt.id}">--}%
                                    %{--<a title="Fashion" class="ma_level_0 is_parent " style=""--}%
                                       %{--href="${createLink(uri: "/browse/${pt.seoFriendlyName}")}">--}%
                                        %{--${rpt.name}--}%
                                        %{--<i class="icon-down-dir-2"></i>--}%
                                    %{--</a>--}%

                                    %{--<div style="background-image: url(${createLink(controller: 'image', params: [id: rpt.id, type: 'productTypeMenu', role: 'e1', wh: '300x300'])})"--}%
                                         %{--class="stmenu_sub style_wide col-md-12" style="display: none;">--}%
                                        %{--<div class="row">--}%
                                            %{--<div class="col-md-12 stmenu_container">--}%
                                                %{--<ul class="row sttlevel1">--}%
                                                    %{--<g:each in="${pt.children?.findAll { !it?.deleted }}" var="pt1">--}%
                                                        %{--<li style="" class="mllevel1 ">--}%
                                                            %{--<a title="Women's Clothing" class="ma_level_1" style=""--}%
                                                               %{--href="${createLink(uri: "/browse/${pt1.seoFriendlyName}")}">--}%
                                                                %{--${pt1.name}--}%
                                                            %{--</a>--}%
                                                            %{--<ul class=" sttlevel2">--}%
                                                                %{--<g:each in="${pt1.children?.findAll { !it?.deleted }}"--}%
                                                                        %{--var="pt2">--}%
                                                                    %{--<li style="" class="mllevel2">--}%
                                                                        %{--<a title="Women's Clothing" class="ma_level_1"--}%
                                                                           %{--style=""--}%
                                                                           %{--href="${createLink(uri: "/browse/${pt2.seoFriendlyName}")}">--}%
                                                                            %{--${pt2.name}--}%
                                                                        %{--</a>--}%
                                                                    %{--</li>--}%
                                                                %{--</g:each>--}%
                                                            %{--</ul>--}%
                                                        %{--</li>--}%
                                                    %{--</g:each>--}%
                                                %{--</ul>--}%
                                                %{--<img src="${createLink(controller: 'image', params: [id: rpt.id, type: 'productTypeMenu', role: 'e1',wh:'300x300'])}">--}%
                                            %{--</div>--}%
                                        %{--</div>--}%
                                    %{--</div>--}%
                                %{--</li>--}%

                            %{--</g:each>--}%
                            %{--<li class="sttlevel0  m_alignment_0" id="st_menu_seller">--}%
                                %{--<a title="Fashion" class="ma_level_0 is_parent " style=""--}%
                                   %{--href="#">--}%
                                    %{--<g:message code="seller-goldaan"/>--}%
                                    %{--<i class="icon-down-dir-2"></i>--}%
                                %{--</a>--}%

                                %{--<div--}%
                                     %{--class="stmenu_sub style_wide col-md-12" style="display: none;">--}%
                                    %{--<div class="row">--}%
                                        %{--<div class="col-md-12 stmenu_container">--}%
                                            %{--<ul class="row sttlevel1">--}%
                                                %{--<g:each in="${eshop.Brand.list()}" var="brand">--}%
                                                    %{--<li style="" class="mllevel1 ">--}%
                                                        %{--<a title="Women's Clothing" class="ma_level_1" style=""--}%
                                                           %{--href="${createLink(uri: "/filter?f=b${brand?.id}")}">--}%
                                                            %{--${brand?.name}--}%
                                                        %{--</a>--}%
                                                    %{--</li>--}%
                                                %{--</g:each>--}%
                                            %{--</ul>--}%
                                        %{--</div>--}%
                                    %{--</div>--}%
                                %{--</div>--}%
                            %{--</li>--}%
                        %{--</ul>--}%
                    %{--</nav>--}%
                    <div id="header" class="header">
                        <div class="navigation">
                            <g:render template="/layouts/goldaan/menuInner"/>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                </div>

                <div class="col-xs-4 pull-left">
                    <div class="basket-cell">
                        <g:render template="/layouts/${grailsApplication.config.eShop.instance}/shoppingBasket"/>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
