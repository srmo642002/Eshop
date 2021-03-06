<%@ page import="eshop.ProductModel" %>
<div class="container-fluid">
    <g:set var="productModelList" value="${eshop.ProductModel.findAllByIdInList(productIds?.collect{it.modelId})}"/>
    <ul class="showbiz thumbnailGrid row-fluid">
        <g:each in="${productIds}" status="i" var="productId">
            <g:set var="productModel" value="${productModelList.find{it.id == productId.modelId}}"/>
            <g:if test="${productModel}">
                <li class="span3">
                    <g:render template="/site/common/productThumbnail" model="[productModel: productModel, productId: productId.id, modelCount: ProductModel.countByProduct(productModel.product)]"/>
                </li>
            </g:if>
            <g:else>
                <eshop:synchronizeProduct ModelId="${productId.modelId}"/>
            </g:else>
        </g:each>
    </ul>
    <g:if test="${filters.products.totalPages > 1}">
        <div class="pagination pagination-centered">
            <ul>
                <g:if test="${params.page && params.page.toInteger() > 1}">
                    <li ${(params.page ?: "0") == it.toString() ? 'class="active"' : ''}>
                        <g:link action="${params.action == 'browse' ? 'filter' : params.action}" params="${params.action == 'browse'? [page:0, f: "p${productType.id}"] : params + [page: 0]}"><<</g:link></li>
                </g:if>
                <g:if test="${params.page && params.page.toInteger() > 0}">
                    <li ${(params.page ?: "0") == it.toString() ? 'class="active"' : ''}>
                        <g:link action="${params.action == 'browse' ? 'filter' : params.action}" params="${params.action == 'browse'? [page: params.page.toInteger() - 1, f: "p${productType.id}"] : params + [page: params.page.toInteger() - 1]}"><</g:link></li>
                </g:if>
                <g:set var="currentPage" value="${0}"></g:set>
                <g:if test="${params.page}">
                    <g:set var="currentPage" value="${params.page.toInteger()}"></g:set>
                </g:if>
                <g:each in="${(0..<filters.products.totalPages + 1)}">
                    <g:if test="${it > currentPage - 6 && it < currentPage + 6}">
                        <li ${(params.page ?: "0") == it.toString() ? 'class="active"' : ''}>
                            <g:link action="${params.action == 'browse' ? 'filter' : params.action}" params="${params.action == 'browse'? [page:it, f: "p${productType.id}"] : params + [page: it]}">${it + 1}</g:link></li>
                    </g:if>
                </g:each>
                <g:if test="${currentPage < filters.products.totalPages - 1}">
                    <li ${(params.page ?: "0") == it.toString() ? 'class="active"' : ''}>
                        <g:link action="${params.action == 'browse' ? 'filter' : params.action}" params="${params.action == 'browse'? [page:currentPage + 1, f: "p${productType.id}"] : params + [page: currentPage + 1]}">></g:link></li>
                </g:if>
                <g:if test="${currentPage < filters.products.totalPages - 2}">
                    <li ${(params.page ?: "0") == it.toString() ? 'class="active"' : ''}>
                        <g:link action="${params.action == 'browse' ? 'filter' : params.action}"
                                params="${params.action == 'browse'? [page:Math.ceil(filters.products.totalPages).toInteger() - 1, f: "p${productType.id}"] : params + [page: Math.ceil(filters.products.totalPages).toInteger() - 1]}">>></g:link></li>
                </g:if>
            </ul>
        </div>
    </g:if>
</div>