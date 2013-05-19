<%@ page import="eshop.ProductTypeBrand" %>
<g:hasErrors bean="${productTypeBrand}">
    <ul class="errors" role="alert">
        <g:eachError bean="${productTypeBrand}" var="error">
            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
    </ul>
</g:hasErrors>

<g:hiddenField name="guarantee.id" value="${productTypeBrand?.guarantee?.id}" />
<g:hiddenField name="id" value="${productTypeBrand?.id}" />
<g:hiddenField name="version" value="${productTypeBrand?.version}" />

<div class="fieldcontain ${hasErrors(bean: guaranteeInstance, field: 'productTypes', 'error')} ">
    <label for="productTypes">
        <g:message code="productTypeBrand.productTypes.label" default="Product Types"/>
    </label>

    <rg:tree bean="${productTypeBrand}" field="productTypes" relationField="parentProduct" width="250px"></rg:tree>
</div>

<div class="fieldcontain ${hasErrors(bean: guaranteeInstance, field: 'brand', 'error')} ">
    <label for="brand">
        <g:message code="productTypeBrand.brand.label" default="Brand"/>
    </label>
    <rg:autocomplete  domainClass="eshop.Brand" id="brand" like="true" value="${productTypeBrand?.brand?.id}" display="${productTypeBrand?.brand}"/>
    <input type="button" value="${message(code: "add")}" onclick="addBrand()">
</div>

