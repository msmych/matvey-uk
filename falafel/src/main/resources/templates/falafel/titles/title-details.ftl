<div class="col gap-16">
    <div class="t1">${title.title}</div>
    <#if title.year?has_content>
        <div class="t3">Year: ${title.year}</div>
    </#if>
    <#if title.director?has_content>
        <div class="t3">Director: ${title.director}</div>
    </#if>
</div>
