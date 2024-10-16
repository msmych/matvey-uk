<#macro movieSaved titleId>
    <div class="row gap-8">
        <div>☑️</div>
        <button hx-get="/falafel/titles/${titleId}" hx-target="#content">
            View
        </button>
    </div>
</#macro>
