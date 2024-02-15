<div>
    <label for="drink-ingredient">Ingredient:</label>
    <select id="drink-ingredient" name="Ingredient">
        <#list ingredients as ingredient>
            <option value="${ingredient.id}">${ingredient.name}</option>
        </#list>
    </select>
    <label for="drink-ingredient-amount">Amount:</label>
    <input id="drink-ingredient-amount" placeholder="Amount"/>
    <button>Add</button>
</div>
