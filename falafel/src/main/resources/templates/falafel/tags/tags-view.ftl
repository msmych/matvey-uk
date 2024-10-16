<div class="row gap-8">
    <#list tags as tag>
        <#if tag.count gt 0 >
            <div>
                ${tag.emoji} ${tag.count}
            </div>
        </#if>
    </#list>
</div>
