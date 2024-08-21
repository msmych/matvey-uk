<#macro item id label name value="" placeholder="" required=true>
    <label class="col gap-2">
        <span class="hint">${label}</span>
        <input type="text"
               name="${name}"
               value="${value}"
               placeholder="${placeholder}"
               <#if required>required</#if>
        />
    </label>
</#macro>
