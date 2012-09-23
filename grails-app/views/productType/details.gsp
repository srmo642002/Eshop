<!doctype html>
<html>
<head>
    <r:require modules="bootstrap-file-upload"/>
    <meta name="layout" content="main">

    <title><g:message code="product.attributeValues.label" default="Product Details"/></title>
</head>

<body>
<h2><g:message code="product.details.label" default="Product Details"/> q${productInstance}</h2>

<div id="details-tabs">
    <ul>
        <li><a href="#variations"><g:message code="variations"/></a></li>
    </ul>

    <div id="variations" style="width: 98%">
        <g:render template="../variationGroup/variations"/>
    </div>

</div>
<g:javascript>
    $(function () {
        $("#details-tabs").tabs();
    });
</g:javascript>
</body>
</html>