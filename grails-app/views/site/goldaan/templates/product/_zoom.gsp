<%@ page import="com.sun.xml.internal.messaging.saaj.util.ByteInputStream; javax.imageio.ImageIO" %>
<g:if test="${productModel?.width?:product?.width}">
    <div class="ruler-top"><g:formatNumber number="${productModel?.width?:product?.width}" type="number" />cm</div>
</g:if>
<div class="${(productModel?.height?:product?.height)?'ruler-right':''}">
<g:if test="${(productModel?.height?:product?.height)}">
    <div class="ruler-right-content pull-right"><g:formatNumber number="${(productModel?.height?:product?.height)}" type="number" />cm</div>
</g:if>

<ul id="etalage">

    %{--<g:set var="mainImage" value="${product?.mainImage}"/>--}%
    %{--<g:if test="${mainImage}">--}%
        %{--<li>--}%
            %{--<a href='${mainImage?.id}' >--}%
                %{--<g:set var="image"--}%
                       %{--value="${ImageIO.read(new ByteInputStream(mainImage?.fileContent, mainImage?.fileContent?.length))}"/>--}%
                %{--<img class="etalage_thumb_image" zoomable="${mainImage?.dynamicProperties?.zoomable ? '1' : '0'}"--}%
                     %{--width="50" height="50" itemprop="image"--}%
                     %{--src="<g:createLink controller="image"--}%
                                        %{--params="[id: product?.id, name: mainImage?.name, wh: '300x300']"/>"/>--}%
                %{--<img class="etalage_source_image"--}%
                     %{--src="<g:createLink controller="image"--}%
                                        %{--params="[id: product?.id, name: mainImage?.name, wh: 'max']"/>"/>--}%
            %{--</a>--}%
        %{--</li>--}%
    %{--</g:if>--}%

    <g:each in="${product?.images?.findAll {it?.variationValues?.collect{it?.id}?.containsAll(productModel?.variationValues?.collect{it?.id}) }}">
        <li>
            <a href='#${it?.id}' >
                %{--<g:set var="image" value="${ImageIO.read(new ByteInputStream(it.fileContent, it.fileContent.length))}"/>--}%
                <img class="etalage_thumb_image"  itemprop="image"  zoomable="${it?.dynamicProperties?.zoomable ? '1' : '0'}"
                     width="50" height="50"
                     src="<g:createLink controller="image" params="[id: product?.id, name: it.name, wh: '300x300']"/>"/>
                <img class="etalage_source_image"
                     src="<g:createLink controller="image" params="[id: product?.id, name: it.name, wh: 'max']"/>"/>
            </a>
        </li>
    </g:each>
</ul>

<!-- Modal -->
<div id="myModal" class="modal hide fade" tabindex="-1" role="window" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    </div>

    <div class="modal-body">
    </div>
</div>

    <div id="aggregateRating"  itemprop="aggregateRating" itemscope itemtype="http://schema.org/AggregateRating" >
        %{--<g:message code="rate"/>:--}%
        %{--<span class="meta" itemprop="value">${rate}</span>--}%
        <meta itemprop="bestRating" content="5">
        <meta itemprop="worstRating" content="0">
        <meta itemprop="ratingValue" content="${rate}">
        <meta itemprop="ratingCount" content="${rateCount}">
        <eshop:rate identifier="hidProductRate" currentValue="${rate}"
                    readOnly="true"/>

        <g:if test="${grailsApplication.config.instance != 'Local'}">
            <div style="float: left;">
                <div class="g-plusone"></div>

                <script type="text/javascript">
                    window.___gcfg = {lang: 'fa'};

                    (function () {
                        var po = document.createElement('script');
                        po.type = 'text/javascript';
                        po.async = true;
                        po.src = 'https://apis.google.com/js/plusone.js';
                        var s = document.getElementsByTagName('script')[0];
                        s.parentNode.insertBefore(po, s);
                    })();
                </script>
            </div>
        </g:if>

    </div>

</div>
<div class="clear-float"></div>




<script language="javascript" type="text/javascript">

    jQuery(document).ready(function ($) {

        $('#etalage').etalage({
            source_image_width: 900,
            source_image_height: 900,
            zoom_area_width: $('#product-description-area').width(),
            zoom_area_height: Math.max($('#product-description-area').height(), 300),
            zoom_area_distance: 20,
            small_thumbs: 4,
            smallthumb_inactive_opacity: 0.3,
            smallthumbs_position: 'bottom',
            show_icon: false,
            align:'right',
            autoplay: false,
            keyboard: false,
            zoom_easing: true,
            show_hint: true,
            click_callback: function (image_anchor, instance_id) {

                $("#myModal .modal-body").html('<img class="loading" src="${resource(dir: 'images', file: 'loading.gif')}"/>');
                $("#myModal").modal({
                    backdrop: false,
                    show: true
                });
                $("#myModal .modal-body").load('${createLink(controller: 'site', action: 'productImage', params: [id: product?.id])}?img=' + image_anchor.replace('#', ''), function() {});
            }
        });

        $(".etalage_small_thumbs ul li img[src$='50x50']").parent().fadeIn();
    });
</script>