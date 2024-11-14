<#import "../components/menu.ftl" as menu>

<div class="col gap-16">
    <div class="t1">Clubs</div>
    <#list clubs as club>
        <div class="row gap-8">
            <div class="t3">${club.name}</div>
            <button>Enter</button>
        </div>
    </#list>
    <button hx-get="/falafel/clubs/new-club-form" hx-target="#content">New club</button>
</div>

<!-- OOB -->
<@menu.menuTitles account=account activeTab="clubs"/>
