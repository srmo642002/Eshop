<%@ page import="eshop.Product" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html dir="rtl">
<head>
    <title><g:message code="site.mainPage.title-goldaan"/></title>
    <!-- E7zma1ATwR6TvWerhh0l7txRVh0 -->
    <meta name="description" content="${message(code: 'site.mainPage.description')}">
    <meta name="keywords" content="${message(code: 'site.mainPage.keywords-goldaan')}">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="author" content="">


    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    %{--<!--[if lt IE 9]>--}%
    <!--<script src="http://html5shim.googlecode.com/svn/trunk/html5" type="text/javascript"/>-->
    %{--<![endif]-->--}%
    <script language="JavaScript">
    var selectedAddedValues = [${selectedAddedValues.collect{it.id}.join(',')}];
    $(document).ready(function () {
        $('#selectedAddedValues').val(selectedAddedValues.toString());
    });
    function addToBasket(id, name, price) {
        var scope = angular.element(document.getElementById('main-container')).scope();
        scope.selectedAddedValues = selectedAddedValues;
        scope.addToBasket(id, name, price, selectedAddedValues);
    }

    function addOrRemoveAddedValue(item) {

        var i = selectedAddedValues.indexOf(parseInt($(item).val()));
        if (i != -1)
            selectedAddedValues.splice(i, 1);
        else
            selectedAddedValues[selectedAddedValues.length] = parseInt($(item).val());

        $('#selectedAddedValues').val(selectedAddedValues.toString());
        var $form = $("#productVariationForm");
        var serializedData = $form.serialize();
        $('#product-card').html('${message(code: 'waiting')}');
        angular.element(document.getElementById('main-container')).scope().reloadProductCart("${createLink(controller: "site", action: "productCard")}", serializedData, $('#product-card'));
    }
    </script>
    %{--<!-- Start Alexa Certify Javascript -->--}%
    %{--<script type="text/javascript">--}%
        %{--_atrk_opts = { atrk_acct:"y2lmi1a8s700WQ", domain:"zanbil.ir",dynamic: true};--}%
        %{--(function() { var as = document.createElement('script'); as.type = 'text/javascript'; as.async = true; as.src = "https://d31qbv1cthcecs.cloudfront.net/atrk.js"; var s = document.getElementsByTagName('script')[0];s.parentNode.insertBefore(as, s); })();--}%
    %{--</script>--}%
    %{--<noscript><img src="https://d5nxst8fruw4z.cloudfront.net/atrk.gif?account=y2lmi1a8s700WQ" style="display:none" height="1" width="1" alt="" /></noscript>--}%
    %{--<!-- End Alexa Certify Javascript -->--}%

</head>

<body>
<table class="layout-container table-simulated">
    <tr class="table-row">
        <td colspan="2">
            <ehcache:render template="common/slideshowMain_goldaan"/>
        </td>
    </tr>
    <tr class="table-row">
        <td colspan="2">
            <div class="mostvisited-nav">
                <g:each in="${mostVisitedProducts}" var="product">
                    <g:render template="goldaan/templates/productThumbnail" model="${[product:product,size:'200x200']}"/>
                </g:each>
            </div>
        </td>
    </tr>
    %{--<tr class="table-row">--}%
        %{--<td colspan="2">--}%
            %{--<table class="table-simulated">--}%
                %{--<tr>--}%
                    %{--<td class="specialSales-cell">--}%
                        %{--<ehcache:render template="common/slideshowSpecialSales"--}%
                                  %{--model="[specialSaleSlides: specialSaleSlides]"/>--}%
                    %{--</td>--}%
                    %{--<td class="namad-cell">--}%
                        %{--<ehcache:render template="banners/enamad"/>--}%
                    %{--</td>--}%
                %{--</tr>--}%
            %{--</table>--}%
        %{--</td>--}%
    %{--</tr>--}%
    <tr class="table-row">
        <td class="table-cell">
            <div class="banners-container">
                <div class="banners-hover">
                    <ehcache:render template="banners/rightsideBanners"/>
                    <ehcache:render template="banners/leftsideBanners"/>
                </div>
            </div>
        </td>
    </tr>
    %{--<tr class="table-row">--}%
        %{--<td class="table-cell" colspan="2">--}%
            %{--<g:render template="common/productCarousel"--}%
                      %{--key="${productTypeId}"--}%
                      %{--model="${[title: message(code: 'product.mostVisited.list', args: ['']), productList: mostVisitedProducts]}"/>--}%
        %{--</td>--}%
    %{--</tr>--}%
    %{--<tr class="table-row">--}%
        %{--<td colspan="2" class="table-cell">--}%
            %{--<% def productService = grailsApplication.classLoader.loadClass('eshop.ProductService').newInstance() %>--}%
            %{--<g:set var="lastVisitedProducts"--}%
                   %{--value="${productService.findLastVisitedProducts(cookie(name: 'lastVisitedProducts'))}"/>--}%
            %{--<g:if test="${lastVisitedProducts && !lastVisitedProducts.isEmpty()}">--}%
                %{--<g:render template="/site/common/productCarousel"--}%
                          %{--model="${[title: message(code: 'product.lastVisited.list'), productList: lastVisitedProducts, mode: 'large']}"/>--}%
            %{--</g:if>--}%
        %{--</td>--}%
    %{--</tr>--}%
    %{--<tr class="table-row">--}%
        %{--<td class="table-cell" colspan="2">--}%
            %{--<g:render template="news/window" model="${[productTypeId: productTypeId]}"/>--}%
        %{--</td>--}%
    %{--</tr>--}%
</table>
<script type="text/javascript">
    (function ($) {
        $('.row-fluid ul.thumbnails li.span6:nth-child(2n + 3)').css('margin-right', '0px');
        $('.row-fluid ul.thumbnails li.span4:nth-child(3n + 4)').css('margin-right', '0px');
        $('.row-fluid ul.thumbnails li.span3:nth-child(4n + 5)').css('margin-right', '0px');
    })(jQuery);
</script>
</body>
</html>