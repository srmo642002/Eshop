<%@ page import="com.sun.xml.internal.messaging.saaj.util.ByteInputStream; javax.imageio.ImageIO" %>

<script language="javascript" src="${resource(dir: 'js', file: 'jquery.etalage.modified.js')}" type="text/javascript"></script>
<link href="${resource(dir: 'css', file: 'jquery.etalage.css')}" rel="stylesheet" type="text/css"/>

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
            autoplay: false,
            keyboard: false,
            zoom_easing: true,
            show_hint: true,
            click_callback: function (image_anchor, instance_id) {

                $("#myModal .modal-body").html('<img class="loading" src="${resource(dir: 'images', file: 'loading.gif')}"/>');
                $("#myModal").modal({
                        show: true
                        });
                $("#myModal .modal-body").load('${createLink(controller: 'site', action: 'productImage', params: [id: product?.id])}?img=' + image_anchor.replace('#', ''), function() {});
            }
        });

        $(".etalage_small_thumbs ul li img[src$='50x50']").parent().fadeIn();
    });
</script>

<ul id="etalage">

    <% def imageService = grailsApplication.classLoader.loadClass('eshop.ImageService').newInstance() %>
    <g:set var="mainImage" value="${productModel?.mainImage ?: product?.mainImage}"/>
    <g:if test="${mainImage && imageService.imageBelongsToModel(mainImage, productModel)}">
        <li>
            <a href='${mainImage?.id}' >
                <g:set var="image"
                       value="${ImageIO.read(new ByteInputStream(mainImage?.fileContent, mainImage?.fileContent?.length))}"/>
                <img class="etalage_thumb_image" zoomable="${mainImage?.dynamicProperties?.zoomable ? '1' : '0'}"
                     width="50" height="50" itemprop="image"
                     src="<g:createLink controller="image"
                                        params="[id: product?.id, name: mainImage?.name, wh: '300x300']"/>"/>
                <img class="etalage_source_image"
                     src="<g:createLink controller="image"
                                        params="[id: product?.id, name: mainImage?.name, wh: 'max']"/>"/>
            </a>
        </li>
    </g:if>
    <g:each in="${product?.images?.findAll { it?.name != mainImage?.name && imageService.imageBelongsToModel(it, productModel) }.sort{it.id}}">
        <li>
            <a href='#${it?.id}' >
                <g:set var="image" value="${ImageIO.read(new ByteInputStream(it.fileContent, it.fileContent.length))}"/>
                <img class="etalage_thumb_image" zoomable="${mainImage?.dynamicProperties?.zoomable ? '1' : '0'}"
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



