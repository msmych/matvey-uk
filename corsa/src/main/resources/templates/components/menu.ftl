<div id="menu" class="split">
    <div id="menu" class="row gap-8">
        <button class="menu-item active" hx-get="/clubs" hx-target="#page" hx-swap="innerHTML">Clubs</button>
        <button class="menu-item" hx-get="/events" hx-target="#page" hx-swap="innerHTML">Events</button>
    </div>
    <div class="row gap-8">
        <div id="me" hx-get="/me" hx-trigger="load" hx-swap="innerHTML"></div>
    </div>
</div>
<style>
    .menu-item {
        background-color: transparent;
        border: none;
        border-bottom-width: thick;
        border-bottom-style: solid;
        border-bottom-color: transparent;
    }
    .menu-item:hover {
        background-color: aliceblue;
    }
    .menu-item.active {
        font-weight: bold;
        border-bottom-color: darkcyan;
        border-bottom-left-radius: 0;
        border-bottom-right-radius: 0;
    }
</style>
