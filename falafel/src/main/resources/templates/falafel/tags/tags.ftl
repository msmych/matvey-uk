<#list tags as tag>
    <button hx-post="/falafel/tags/${tag.name}?titleId=${titleId}" hx-target="#tag-${titleId}">
        ${tag.emoji} <#if tag.count gt 0>${tag.count}</#if>
    </button>
</#list>