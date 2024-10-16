<#macro menu account activeTab="titles" oob=false>
    <div id="menu" class="col menu gap-16" <#if oob>hx-swap-oob="true"</#if>>
        <#if account?has_content>
            <button class="tab <#if activeTab == "account">active</#if>"
                    hx-get="/falafel/me"
                    hx-target="#content"
                    hx-push-url="true"
            >
                ${account.name} 🍿${account.currentBalance}
            </button>
        </#if>
        <#if !account?has_content>
            <a class="tab click" href="/login">Login</a>
        </#if>
        <button class="tab <#if activeTab == "titles">active</#if>"
                hx-get="/falafel/titles"
                hx-target="#content"
                hx-push-url="true"
        >
            🎞️ Titles
        </button>
        <button class="tab <#if activeTab == "tags">active</#if>"
                hx-get="/falafel/tags"
                hx-target="#content"
                hx-push-url="true"
        >
            🏷️ Tags
        </button>
        <button class="tab <#if activeTab == "tmdb">active</#if>"
                hx-get="/falafel/tmdb"
                hx-target="#content"
                hx-push-url="true"
        >
            🗄️ TMDb
        </button>
    </div>
    <style>
        .menu {
            flex-basis: 10em;
        }

        .tab.active {
            font-weight: bold;
        }
    </style>
</#macro>
