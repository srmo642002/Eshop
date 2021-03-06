
<link href="${resource(dir: 'css', file: 'jquery.themepunch.showbizpro.css')}" rel="stylesheet" type="text/css"/>
<script language="javascript" src="${resource(dir: 'js', file: 'jquery.themepunch.plugins.js')}" type="text/javascript"></script>
<script language="javascript" src="${resource(dir: 'js', file: 'jquery.themepunch.showbizpro.js')}" type="text/javascript"></script>
<h3 class="showbiz-header">${title}</h3>
<g:set var="id" value="${java.util.UUID.randomUUID()}"></g:set>
<table class="table-simulated product-carousel-container">
    <tr class="table-row">
        <td class="showbiz-navigation center sb-nav-dark table-cell">

            <div class="sb-navigation-right" id="showbiz_right_${id}"><i class="icon-right-open"></i></div>

        </td>

        <td id="carousel_${id}" class="showbiz-container table-cell">
            <div class="showbiz" data-left="#showbiz_left_${id}" data-right="#showbiz_right_${id}">
                <div class="overflowholder">
                    <ul class="product-carousel">
                        <g:each in="${productList}" var="product">
                            <li class="sb-showcase-skin">
                                <g:render template="/site/common/productThumbnailSmall"
                                          model="${[product: product]}"/>
                            </li>
                        </g:each>
                    </ul>

                    <div class="sbclear"></div>
                </div>

                <div class="sbclear"></div>
            </div>
        </td>

        <td class="showbiz-navigation center sb-nav-dark table-cell">
            <div class="sb-navigation-left" id="showbiz_left_${id}"><i class="icon-left-open"></i></div>

        </td>
    </tr>
</table>

<div class="sb-clear"></div>

<script type="text/javascript">

    jQuery(document).ready(function () {

        var itemsCount = ${productList.count{it}};

        var width = $('#carousel_${id}').width();
        var visibleCount = Math.floor(width / 204);

        if (itemsCount > visibleCount) {
            jQuery('#carousel_${id}').showbizpro({
                elementSize: 204
            });
        }
        else {
            var carousel = $('#carousel_${id} .product-carousel');
            carousel.parent().parent().parent().parent().replaceWith(carousel);
            carousel.removeClass('product-carousel').addClass('static-product-carousel').addClass('showbiz');
        }
    });

</script>



