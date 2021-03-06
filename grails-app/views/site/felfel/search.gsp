<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html dir="rtl">
<head>
    <g:if test="${title}">
        <title>${title}</title>
    </g:if>
    <meta charset="utf-8">
    <g:if test="${description}">
        <meta name="description" content="${description}">
    </g:if>
    <g:if test="${keywords}">
        <meta name="keywords" content="${keywords}">
    </g:if>
    <g:render template="common/productGridMeta"
              model="${[productIds: filters.products.productIds]}"/>
    <link href="${resource(dir: 'css', file: 'site.css')}" rel="stylesheet" type="text/css"/>

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <script language="JavaScript" type="text/javascript"
            src="${resource(dir: 'js', file: 'jquery.transform.js')}"></script>
    <link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'toggles-felfel.css')}"/>
    <script language="javascript" type="text/javascript" src="${resource(dir: 'js', file: 'toggles.min.js')}"></script>
    <link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'bootstrap-select.css')}"/>
    <script language="javascript" type="text/javascript"
            src="${resource(dir: 'js', file: 'bootstrap-select.min.js')}"></script>

    <script language="javascript" type="text/javascript"
            src="${resource(dir: "js/${grailsApplication.config.eShop.instance}", file: 'ajaxFilter.js')}"></script>

</head>

<body>

<div id="breadcrumb">
    <ul class="breadcrumb">
        <li itemscope itemtype="http://data-vocabulary.org/Breadcrumb">
            <a class="home" href="${createLink(uri: '/')}" itemprop="url">
                <span itemprop="title">
                    <g:message code="home"/>
                </span>
            </a>
        </li>
        <li itemscope itemtype="http://data-vocabulary.org/Breadcrumb">
            <span class="divider">${"/"}</span>
            <a href="${request.requestURI.replace('.dispatch', '').replace('grails/site/', '') + '?' + request.queryString}"
               itemprop="url">
                <span itemprop="title">
                    <g:message code="search.for.label"></g:message> ${params.phrase}
                </span>
            </a>
        </li>
        <g:if test="${filters.breadcrumb.size() > 0}">
            <g:each in="${filters.breadcrumb[0..-1]}">
                <li itemscope itemtype="http://data-vocabulary.org/Breadcrumb">
                    <span class="divider">${"/"}</span>
                    <a href="${commonLink}${it.linkTail}"
                       itemprop="url"><span itemprop="title">${it.linkTitle}</span></a>
                </li>
            </g:each>
        </g:if>
    </ul>
</div>

<div class="clearfix"></div>

<h3 class="category_heading top_less bottom_less" id="title">
    %{--<div class="right_text">--}%
    %{--<g:message code="menu.startPrice"/> ${eshop.productTypeMinPrice(productTypeId: productTypeId)} <eshop:currencyLabel/>--}%
    %{--</div>--}%
    <g:message code="search.for.label"></g:message> ${params.phrase}
</h3>

<g:render template="common/statusFilter"/>
<div class="clearfix"></div>

<div class="toolbar_top">
    <span id="graphicalFilter">
        <g:render template="/site/common/priceRangeSlider"/>
    </span>
    <span id="pagination">
        <g:render template="/site/common/pagination" model="${totalPages = filters.products.totalPages}"/>
    </span>

    <div class="clearfix"></div>
</div>

<div class="filter_left">
    <div class="filter_float_threshold_start"></div>

    <div class="floating_filter" id="filterBar">
        <g:render template="common/filteringTextualMenu"/>
    </div>
</div>

<div id="products" class="listing_right">

    <g:render template="common/productRowList"
              model="${[productIds: filters.products.productIds]}"/>
</div>

<div class="clearfix"></div>

<div class="toolbar_bottom">
    <div class="toolbar_top">
        <span id="secondPagination">
            <g:render template="/site/common/pagination" model="${totalPages = filters.products.totalPages}"/>
        </span>

        <div class="clearfix"></div>
    </div>
</div>
<script language="JavaScript" type="text/javascript">
    $(document).ready(function () {
        setFilterSize();
        resetStickForFilters();
    });

    function resetStickForFilters() {
        $(".floating_filter").stick_in_parent({
            offset_top: 75
        });
    }

    function setFilterSize() {
        $('.filter_left').css('height', Math.max($('.filter_left').height(), $('.listing_right').height()));
    }
</script>
</body>
</html>