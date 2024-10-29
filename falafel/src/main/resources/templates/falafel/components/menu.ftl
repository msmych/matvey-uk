<#macro menuTitles account activeTab="titles">
    <#if account?has_content>
        <span id="menu-tab-account"
              <#if activeTab=="account">style="font-weight: bold"</#if>
              hx-swap-oob="true"
        >${account.name}</span>
    <#else>
        <span id="menu-tab-account"
              <#if activeTab=="account">style="font-weight: bold"</#if>
              hx-swap-oob="true"
        >Login</span>
    </#if>
    <span id="menu-tab-titles"
          <#if activeTab=="titles">style="font-weight: bold"</#if>
          hx-swap-oob="true"
    >🎞️ Titles</span>
    <span id="menu-tab-tmdb"
          <#if activeTab=="tmdb">style="font-weight: bold"</#if>
          hx-swap-oob="true"
    >🗄️ TMDb</span>
    <span id="menu-tab-tags"
          <#if activeTab=="tags">style="font-weight: bold"</#if>
          hx-swap-oob="true"
    >🏷️ Tags</span>
</#macro>
