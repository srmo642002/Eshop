%{--product types--}%
<g:if test="${productType.children}">
    <h3 class="productType-container-title">
        <g:message code="site.selectSubcategory"
                   default="Select SubProductType"></g:message>
    </h3>
    <g:render template="common/productTypeGrid"></g:render>
</g:if>
%{--Brands Filters--}%
<g:if test="${filters?.brands}">
    <g:render template="common/brandCarousel" model="${[
            title: message(code: 'site.selectBrand'),
            productType: productType,
            brands: filters.brands
    ]}"></g:render>
</g:if>
%{--attributes--}%
<g:if test="${filters?.attributes}">
    <g:render template="common/productAttributeList"></g:render>
</g:if>