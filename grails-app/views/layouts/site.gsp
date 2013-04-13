<%@ page import="eshop.ProductService" %>
<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js" ng-app='eshop'><!--<![endif]-->
<head>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'siteUI.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'bootstrap/css', file: 'bootstrap.min.css', plugin: 'rapid-grails')}"/>
    <link rel="stylesheet"
          href="${resource(dir: 'bootstrap/css', file: 'bootstrap-responsive.min.css', plugin: 'rapid-grails')}"/>
    %{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-amazon.css')}"/>--}%
    <link rel="stylesheet" href="${resource(dir: 'bootstrap/css', file: 'bootstrap-rtl.css', plugin: 'rapid-grails')}"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'zanbil.css')}"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'site.css')}"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.rollbar.css')}"/>

    <g:javascript library="jquery"></g:javascript>

    <r:layoutResources/>
    <script type="text/javascript"
            src="${resource(plugin: 'rapid-grails', dir: 'js', file: 'angular.min.js')}"></script>
    <script type="text/javascript">
        var basketCounter = ${session.getAttribute("basketCounter") ?: 0};
        var basket = ${(session.getAttribute("basket")?: []) as grails.converters.JSON};
        var contextRoot = "${createLink(uri: '/')}";
    </script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'eshopCtrl.js')}"></script>
    <g:javascript src="jquery.rollbar.js"></g:javascript>
    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.tpl_layout1.1.6.min.js')}"
            type="text/javascript"></script>
    <g:layoutHead/>
</head>

<body dir="rtl">
<div id="main-container" ng-controller="eshopCtrl">
    <g:render template="/layouts/header"></g:render>
    <div id="body-container">
        <g:layoutBody/>
        <g:render template="/layouts/footer"></g:render>
    </div>
    <r:layoutResources/>
</div>
<g:javascript library="jquery"/>
<script src="${resource(dir: 'bootstrap/js', file: 'bootstrap.min.js', plugin: 'rapid-grails')}"></script>
<g:javascript src="jquery.mousewheel.js"></g:javascript>
<g:javascript src="jcarousellite.js"></g:javascript>

</body>
</html>
