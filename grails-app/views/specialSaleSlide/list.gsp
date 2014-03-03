<%@ page import="eshop.SpecialSaleSlide" %>
<!doctype html>
<html>
<head>

    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'specialSaleSlide.label', default: 'Special Sale Slide')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<script language="javascript" src="${resource(dir: 'js', file: 'jquery.quickselect.pack.js')}" type="text/javascript"></script>
<h2><g:message code="default.manage.label" args="[entityName]"/> ${eshop.Product.get(params.id)}</h2>

<div class="content scaffold-list" ng-controller="specialSaleSlideController" role="main">
    <rg:grid domainClass="${SpecialSaleSlide}"
             showCommand="false"
             maxColumns="5"
             commands="${[[handler: "deleteSpecialSaleSlide(#id#)", icon: "application_delete"]]}">
        <rg:criteria>
            <rg:eq name="product.id" value="${params.id}"/>
        </rg:criteria>
    </rg:grid>
    <rg:dialog id="specialSaleSlide" title="${message(code: "specialSaleSlide")}">
        <rg:fields bean="${new SpecialSaleSlide()}">
            <rg:template>
                <div>
                    <div class="form-fields-part">
                        <input type="hidden" name="product.id" id="product.id" value="${params.id}"/>
                        <rg:field name="price"/>
                        <rg:field name="totalCount"/>
                        <rg:field name="remainingCount"/>

                        <div class="fieldcontain required">
                            <label for="image"><g:message code="slide.image.label" default="image"/>
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="file" id="image" name="image"/>
                        </div>

                    </div>

                    <div class="form-fields-part">
                        <rg:field name="startDate"/>

                        <div class="fieldcontain required">
                            <label for="extraImage1"><g:message code="slide.extraImage1.label" default="extraImage1"/>
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="file" id="extraImage1" name="extraImage1"/>
                        </div>


                        <div class="fieldcontain required">
                            <label for="extraImage2"><g:message code="slide.extraImage2.label" default="extraImage2"/>
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="file" id="extraImage2" name="extraImage2"/>
                        </div>

                        <div class="fieldcontain required">
                            <label for="extraImage3"><g:message code="slide.extraImage3.label" default="extraImage3"/>
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="file" id="extraImage3" name="extraImage3"/>
                        </div>
                    </div>

                    <div class="form-fields-part">
                        <rg:field name="finishDate"/>
                        <rg:field name="extraLink1"/>
                        <rg:field name="extraLink2"/>
                        <rg:field name="extraLink3"/>
                    </div>
                </div>
            </rg:template>
        </rg:fields>
        <rg:saveButton domainClass="${SpecialSaleSlide}"/>
        <rg:cancelButton/>
    </rg:dialog>
    <input type="button" ng-click="openSpecialSaleSlideCreateDialog()" value="<g:message code="new"/>"/>
    <input type="button" ng-click="openSpecialSaleSlideEditDialog()" value="<g:message code="edit"/>"/>
    <input type="button" value="${message(code:'specialSaleSlides.returnToProductDetail')}" onclick="window.location.href='${createLink(controller: 'product', action:'productDetails', params:[id:params.detail])}#price'">
    <script language="javascript" type="text/javascript">
        function deleteSpecialSaleSlide(id){
             if (confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}')) {
                var url = "<g:createLink action="delete"/>";
                $.ajax({
                    type:"POST",
                    url:url,
                    data:{ id:id }
                }).done(function (response) {
                    if (response == "0") {
                        var grid = $("#SpecialSaleSlideGrid");
                        grid.trigger('reloadGrid');
                    }
                    else {
                    }
                });
            }
        }
        $(function(){
            $( "#specialSaleSlide" ).on( "dialogopen", function( event, ui ) {
                setTimeout("$(\"select.compositionField:visible\").quickselect()",100)
            } );
            $("[ng-click^=addCompositebasketProductCriteria],[ng-click^=addCompositespecialSaleSlideProductsCriteria]").click(function(){
                setTimeout("$(\"select.compositionField:visible\").quickselect()",100)
            })
        })
    </script>
</div>
</body>
</html>
