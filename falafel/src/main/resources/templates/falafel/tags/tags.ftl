<#list tags as tag>
    <button hx-post="/falafel/tags/${tag.name}?titleId=${titleId}" hx-target="#tag-${titleId}">
        ${tag.emoji} <#if tag.count gt 0>${tag.count}</#if>
    </button>
</#list>
<!-- OOB -->
<button id="account-tab"
        hx-get="/me"
        hx-target="#content"
        hx-swap-oob="true"
>
    ${account.name} 🍿${account.currentBalance}
</button>
