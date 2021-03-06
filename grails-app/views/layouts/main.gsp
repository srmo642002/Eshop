<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js" ng-app><!--<![endif]-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="${message(code:'title')}"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${resource(plugin: 'rapid-grails', dir: 'css', file: 'rapidgrails.css')}"
          type="text/css">
    <link href="${resource(dir: 'css', file: 'main.css')}" rel="stylesheet" type="text/css"/>
    <link href="${resource(dir: 'css', file: 'mobile.css')}" rel="stylesheet" type="text/css"/>
    <link href="${resource(dir: 'css', file: 'css3.css')}" rel="stylesheet" type="text/css"/>
    <link href="${resource(dir: 'css', file: 'theme.css')}" rel="stylesheet" type="text/css"/>
    <link href="${resource(dir: 'css', file: 'msgGrowl.css')}" rel="stylesheet" type="text/css"/>

    <ckeditor:resources/>
    <g:javascript library="jquery"/>
    <r:layoutResources/>
    <jqui:resources theme="${grailsApplication.config.admin.theme}"/>

    <script src="${resource(dir: 'js', file: 'angular.min.js', plugin: 'rapid-grails')}" language="javascript"
            type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'angular.autocomplete.js', plugin: 'rapid-grails')}" language="javascript"
            type="text/javascript"></script>

    <script src="${resource(dir: 'js', file: 'utils.js', plugin: 'rapid-grails')}" language="javascript"
            type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'grid_utils.js', plugin: 'rapid-grails')}" language="javascript"
            type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'jquery.json-2.3.min.js', plugin: 'rapid-grails')}" language="javascript"
            type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'jquery.form.js', plugin: 'rapid-grails')}" language="javascript"
            type="text/javascript"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.selectbox.js')}"></script>

    <link rel="stylesheet"
          href="${resource(dir: 'css/datepicker', file: 'ui.datepicker.css', plugin: 'rapid-grails')}"/>
    <script src="${resource(dir: 'js/datepicker', file: 'jquery.ui.datepicker-cc.js', plugin: 'rapid-grails')}"
            language="javascript" type="text/javascript"></script>
    <script src="${resource(dir: 'js/datepicker', file: 'calendar.js', plugin: 'rapid-grails')}" language="javascript"
            type="text/javascript"></script>
    <script src="${resource(dir: 'js/datepicker', file: 'jquery.ui.datepicker-cc-fa.js', plugin: 'rapid-grails')}"
            language="javascript" type="text/javascript"></script>

    <rg:jqgridResources/>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.labelify.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'theme.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.maskinput.js')}"></script>


    <g:layoutHead/>
    <rg:jstreeResources/>

    <script language="javascript" src="${resource(dir: 'js', file: 'jquery.msgGrowl.js')}"
            type="text/javascript"></script>


    %{--easy ui--}%
    <link href="${resource(dir: 'css/jquery.easyui/metro', file: 'easyui.css')}" rel="stylesheet" type="text/css"/>
    <link href="${resource(dir: 'css/jquery.easyui', file: 'easyui-rtl.css')}" rel="stylesheet" type="text/css"/>
    %{--<script type="text/javascript" src="${resource(dir: 'js/jquery.easyui', file: 'jquery.draggable.js')}"></script>--}%
    <script type="text/javascript" src="${resource(dir: 'js/jquery.easyui', file: 'jquery.panel.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/jquery.easyui', file: 'jquery.parser.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/jquery.easyui', file: 'jquery.validatebox.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/jquery.easyui', file: 'jquery.tree.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/jquery.easyui', file: 'jquery.combo.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/jquery.easyui', file: 'jquery.combotree.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/jquery.easyui', file: 'jquery.combobox.js')}"></script>
</head>

<body dir="rtl">
<div id="bokeh"><div id="container">
    <div id="header" role="banner">
        <h1 id="logo" style="background-image: url('${resource(dir: 'images', file: "logo-${grailsApplication.config.eShop.instance}.png")}')">Admin Control Panel</h1>
    </div><!-- end #header -->

    <div id="content">
        <div class="panel-controls nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/admin')}"><g:message code="default.home.label"/></a></li>
                <sec:ifLoggedIn>
                    <sec:ifAllGranted roles="${eshop.RoleHelper.ROLE_USER}">
                        <li><a class="profile" href="<g:createLink controller="user" action="profile"
                                                                   params="[redirect: request.requestURI]"/>"><g:message
                                code="profile"/></a></li>
                    </sec:ifAllGranted>
                    <li>
                        <common:logoutLink class="logout"></common:logoutLink>
                        %{--<a class="logout" href="<g:createLink controller="logout"/>"><g:message code="logout"/></a>--}%
                    </li>
                </sec:ifLoggedIn>
                <sec:ifNotLoggedIn>
                    <li>
                        <common:loginLink class="login"></common:loginLink>
                        %{--<a class="login" href="<g:createLink controller="login"/>"><g:message code="login"/></a>--}%
                    </li>
                </sec:ifNotLoggedIn>
            </ul>
        </div>
        <g:layoutBody/>
    </div>
</div>
</div>


<div id="footer" role="contentinfo">
    <g:message code="application.name" default="EShop"/> | <g:message code="application.copyRight"
                                                                      default="© AGAH-IT 2012"/> | <g:message
            code="version"/>
</div><!-- end #footer -->

<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt"
                                                                   default="Loading&hellip;"/></div>
<g:javascript library="application"/>
<r:layoutResources/>

<script>

    function msgSuccess(text) {
        $.msgGrowl({
            type: 'success', 'text': text, position: 'bottom-left'
        });
    }

    function msgInfo(text) {
        $.msgGrowl({
            type: 'info', 'text': text, position: 'bottom-left'
        });
    }

    function msgWarning(text) {
        $.msgGrowl({
            type: 'warning', 'text': text, position: 'bottom-left'
        });
    }

    function msgError(text) {
        $.msgGrowl({
            type: 'error', 'text': text, position: 'bottom-left'
        });
    }
</script>
<g:render template="/events_push"/>
<g:render template="/layouts/${grailsApplication.config.eShop.instance}/google_analytics"/>
</body>
</html>
