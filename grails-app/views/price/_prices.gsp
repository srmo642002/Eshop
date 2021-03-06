<%@ page import="eshop.Price" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'price.label', default: 'Price')}"/>
</head>

<body>

<div class="content scaffold-list" role="main">
    <rg:grid domainClass="${Price}"
             showCommand="false"
             maxColumns="5"
             toolbarCommands="${[[caption: message(code: "add"), function: "addToPriceGrid", icon: "plus"]]}"
             commands="${[[loadOverlay: "${g.createLink(action: "form",controller: "price", params: ['productModel.id': productModelInstance.id])}&id=#id#",saveAction:"${g.createLink(action: "save",controller: "price")}", icon: "application_edit"], [handler: "deletePrice(#id#)", icon: "application_delete"]]}">
        <rg:criteria>
            <rg:eq name="productModel.id" value="${productModelInstance.id}"/>
        </rg:criteria>
    </rg:grid>

    <fieldset class="buttons">

        <g:link class="list" controller="product" action="productDetails" params="[curtab: 3, pid:productModelInstance?.product?.id]"><g:message code="default.productModel.list"
                                                                               default="Product List"/></g:link>
    </fieldset>

    <script language="javascript" type="text/javascript">
        function deletePrice(id){
             if (confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}')) {
                var url = "<g:createLink action="delete" controller="price"/>";
                $.ajax({
                    type:"POST",
                    url:url,
                    data:{ id:id }
                }).done(function (response) {
                    if (response == "0") {
                        var grid = $("#PriceGrid");
                        grid.trigger('reloadGrid');
                    }
                    else {
                    }
                });
            }
        }
        function addToPriceGrid(){
            loadOverlay('<g:createLink controller="price" action="form" params="['productModel.id': productModelInstance.id]"/>','<g:createLink action="save" controller="price"/>',function(){
                $("#PriceGrid").trigger("reloadGrid")
            });
        }
    </script>
</div>
</body>
