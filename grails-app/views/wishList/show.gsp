<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html dir="rtl">
<head>
    <title>Zanbil</title>
    <meta name="layout" content="site">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link rel="stylesheet" href="${resource(dir: 'bootstrap/css', file: 'bootstrap.min.css', plugin: 'rapid-grails')}"/>
    <link rel="stylesheet"
          href="${resource(dir: 'bootstrap/css', file: 'bootstrap-responsive.min.css', plugin: 'rapid-grails')}"/>
    %{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-amazon.css')}"/>--}%
    <link rel="stylesheet" href="${resource(dir: 'bootstrap/css', file: 'bootstrap-rtl.css', plugin: 'rapid-grails')}"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'site.css')}"/>
    <style>
    body {
        padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
    }
    </style>

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Le fav and touch icons -->
    %{--<link rel="shortcut icon" href="../assets/ico/favicon.ico">--}%
    %{--<link rel="apple-touch-icon-precomposed" sizes="144x144" href="../assets/ico/apple-touch-icon-144-precomposed.png">--}%
    %{--<link rel="apple-touch-icon-precomposed" sizes="114x114" href="../assets/ico/apple-touch-icon-114-precomposed.png">--}%
    %{--<link rel="apple-touch-icon-precomposed" sizes="72x72" href="../assets/ico/apple-touch-icon-72-precomposed.png">--}%
    %{--<link rel="apple-touch-icon-precomposed" href="../assets/ico/apple-touch-icon-57-precomposed.png">--}%

    <g:javascript>
        function addToBasket(id, title, price) {
            var scope = angular.element(document.getElementById('main-container')).scope();
            scope.addToBasket(id, title, price);
            scope.$apply();
        }
        function addToCompareList(id, title, price) {
            var scope = angular.element(document.getElementById('main-container')).scope();
            scope.addToCompareList(id, title, price);
            scope.$apply();
        }
    </g:javascript>
</head>

<body>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="shopping-basket">
                <h2><g:message code="wishList"/></h2>

                <div class="group">
                    <ul>
                        <li ng-repeat="wishListItem in wishList">
                            <span class="image"><img ng-src="{{contextRoot}}site/image/{{wishListItem.id}}?wh=100x100"/>
                            </span>
                            <span class="name"><h3><a
                                    ng-href="{{contextRoot}}site/product/{{wishListItem.id}}">{{wishListItem.title}}</a>
                            </h3>
                            </span>
                            <span class="price"><g:message code="price"></g:message>: <b>{{wishListItem.price}}</b>
                            </span>
                            <span>
                                <a href="" class="btn btn-primary btn-buy"
                                   onclick="addToBasket({{wishListItem.id}}, '{{wishListItem.title}}', '{{wishListItem.price}}')"><span><g:message code="add-to-basket"></g:message></span>
                                </a>
                                <a href="" class="btn btn-compare"
                                   onclick="addToCompareList({{wishListItem.id}}, '{{wishListItem.title}}', '{{wishListItem.price}}')"><span><g:message code="add-to-compareList"></g:message></span>
                                </a>
                            </span>
                            <span class="delete">[ <a type="button"
                                                      ng-click="removeFromBasket(wishListItem.id)"><g:message
                                        code="application_delete"></g:message></a> ]</span>
                        </li>
                    </ul>
                </div>
                <sec:ifNotLoggedIn>
                    <div class="info">
                        <div><g:message code="wishList.notEnabled.message"></g:message></div>
                        <common:loginLink class="btn btn-success"></common:loginLink>
                        <common:registerLink class="btn btn-primary"></common:registerLink>
                    </div>
                </sec:ifNotLoggedIn>
            </div>
        </div>
    </div>
</div> <!-- /container -->

<script type="text/javascript">
    (function ($) {
        $('.row-fluid ul.thumbnails li.span6:nth-child(2n + 3)').css('margin-right', '0px');
        $('.row-fluid ul.thumbnails li.span4:nth-child(3n + 4)').css('margin-right', '0px');
        $('.row-fluid ul.thumbnails li.span3:nth-child(4n + 5)').css('margin-right', '0px');
    })(jQuery);
</script>
</body>
</html>