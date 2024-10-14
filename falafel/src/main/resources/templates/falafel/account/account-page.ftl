<#import "../components/menu.ftl" as menu/>

<div class="col gap-16">
    <div class="t1">${account.name}</div>
    <div>Balance: 🍿 ${account.currentBalance}</div>
</div>

<!-- OOB -->
<@menu.menu account=account activeTab="account" oob=true />
