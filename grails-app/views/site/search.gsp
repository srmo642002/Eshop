<%--
  Created by IntelliJ IDEA.
  User: Farzin
  Date: 5/13/13
  Time: 3:10 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>

<div id="header">
    <h1><a href="http://grails.org/Searchable+Plugin" target="_blank">Grails <span>Searchable</span> Plugin</a></h1>
    <g:form url='[controller: "searchable", action: "index"]' id="searchableForm" name="searchableForm" method="get">
        <g:textField name="q" value="${params.q}" size="50"/> <input type="submit" value="Search" />
    </g:form>
    <div style="clear: both; display: none;" class="hint">See <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">Lucene query syntax</a> for advanced queries</div>
</div>
<div id="main">
    <g:set var="haveQuery" value="${params.q?.trim()}" />
    <g:set var="haveResults" value="${searchResult?.results}" />
    <div class="title">
        <span>
            <g:if test="${haveQuery && haveResults}">
                Showing <strong>${searchResult.offset + 1}</strong> - <strong>${searchResult.results.size() + searchResult.offset}</strong> of <strong>${searchResult.total}</strong>
                results for <strong>${params.q}</strong>
            </g:if>
            <g:else>
                &nbsp;
            </g:else>
        </span>
    </div>

    <g:if test="${haveQuery && !haveResults && !parseException}">
        <p>Nothing matched your query - <strong>${params.q}</strong></p>
        <g:if test="${!searchResult?.suggestedQuery}">
            <p>Suggestions:</p>
            <ul>
                <li>Try a suggested query: <g:link controller="searchable" action="index" params="[q: params.q, suggestQuery: true]">Search again with the <strong>suggestQuery</strong> option</g:link><br />
                    <em>Note: Suggestions are only available when classes are mapped with <strong>spellCheck</strong> options, either at the class or property level.<br />
                        The simplest way to do this is add <strong>spellCheck "include"</strong> to the domain class searchable mapping closure.<br />
                        See the plugin/Compass documentation Mapping sections for details.</em>
                </li>
            </ul>
        </g:if>
    </g:if>

    <g:if test="${searchResult?.suggestedQuery}">
        <p>Did you mean <g:link controller="searchable" action="index" params="[q: searchResult.suggestedQuery]">${StringQueryUtils.highlightTermDiffs(params.q.trim(), searchResult.suggestedQuery)}</g:link>?</p>
    </g:if>

    <g:if test="${parseException}">
        <p>Your query - <strong>${params.q}</strong> - is not valid.</p>
        <p>Suggestions:</p>
        <ul>
            <li>Fix the query: see <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">Lucene query syntax</a> for examples</li>
            <g:if test="${LuceneUtils.queryHasSpecialCharacters(params.q)}">
                <li>Remove special characters like <strong>" - [ ]</strong>, before searching, eg, <em><strong>${LuceneUtils.cleanQuery(params.q)}</strong></em><br />
                    <em>Use the Searchable Plugin's <strong>LuceneUtils#cleanQuery</strong> helper method for this: <g:link controller="searchable" action="index" params="[q: LuceneUtils.cleanQuery(params.q)]">Search again with special characters removed</g:link></em>
                </li>
                <li>Escape special characters like <strong>" - [ ]</strong> with <strong>\</strong>, eg, <em><strong>${LuceneUtils.escapeQuery(params.q)}</strong></em><br />
                    <em>Use the Searchable Plugin's <strong>LuceneUtils#escapeQuery</strong> helper method for this: <g:link controller="searchable" action="index" params="[q: LuceneUtils.escapeQuery(params.q)]">Search again with special characters escaped</g:link></em><br />
                    <em>Or use the Searchable Plugin's <strong>escape</strong> option: <g:link controller="searchable" action="index" params="[q: params.q, escape: true]">Search again with the <strong>escape</strong> option enabled</g:link></em>
                </li>
            </g:if>
        </ul>
    </g:if>

    <g:if test="${haveResults}">
        <div class="results">
            <g:each var="result" in="${searchResult.results}" status="index">
                <div class="result">
                    <g:set var="className" value="${ClassUtils.getShortName(result.getClass())}" />
                    <g:set var="link" value="${createLink(controller: className[0].toLowerCase() + className[1..-1], action: 'show', id: result.id)}" />
                    <div class="name"><a href="${link}">${className} #${result.id}</a></div>
                    <g:set var="desc" value="${result.toString()}" />
                    <g:if test="${desc.size() > 120}"><g:set var="desc" value="${desc[0..120] + '...'}" /></g:if>
                    <div class="desc">${desc.encodeAsHTML()}</div>
                    <div class="displayLink">${link}</div>
                </div>
            </g:each>
        </div>

        <div>
            <div class="paging">
                <g:if test="${haveResults}">
                    Page:
                    <g:set var="totalPages" value="${Math.ceil(searchResult.total / searchResult.max)}" />
                    <g:if test="${totalPages == 1}"><span class="currentStep">1</span></g:if>
                    <g:else><g:paginate controller="searchable" action="index" params="[q: params.q]" total="${searchResult.total}" prev="&lt; previous" next="next &gt;"/></g:else>
                </g:if>
            </div>
        </div>
    </g:if>
</div>
</body>
</html>