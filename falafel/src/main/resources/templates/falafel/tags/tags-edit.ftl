<#import "../components/menu.ftl" as menu>

<#list tags as tag>
    <button hx-post="/falafel/tags/${tag.name}?titleId=${titleId}"
            hx-target="#tags-${titleId}"
            <#if account.currentBalance lte 0>disabled</#if>
    >
        ${tag.emoji} <#if tag.count gt 0>${tag.count}</#if>
    </button>
</#list>
