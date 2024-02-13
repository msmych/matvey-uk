<div>
    <div>
        ${drink.name}
    </div>
    <div>
        Ingredients:
        <button hx-get="/drinks/new-ingredient" hx-target="this">
            + Ingredient
        </button>
    </div>
    <div>
        ${drink.recipe!"🤷‍♀️"}
    </div>
</div>
