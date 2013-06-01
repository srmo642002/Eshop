<div class="container-fluid">
    <ul class="thumbnails row-fluid productTypes">
        <g:each in="${subProductTypeLinks}" var="subProductTypeLink">
            <li class="span3">
                <g:if test="${type=='filter'}">
                    <g:render template="common/productTypeThumbnail"
                              model="${[link: [id:subProductTypeLink._id.id, name:subProductTypeLink._id.name, href:createLink(controller: "site", action: "filter", params: params + [f : "${params.f},p${subProductTypeLink._id.id}"])]]}"></g:render>
                </g:if>
                <g:else>
                    <g:render template="common/productTypeThumbnail"
                              model="${[link: subProductTypeLink]}"></g:render>
                </g:else>
            </li>
        </g:each>
    </ul>
</div>