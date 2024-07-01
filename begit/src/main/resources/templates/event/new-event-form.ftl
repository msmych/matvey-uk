<form hx-post="/events" hx-target="#club-${clubId}-events" hx-swap="innerHTML">
    <input id="clubId" type="hidden" name="clubId" value="${clubId}"/>
    <label>
        <input id="title" name="title" type="text" required/>
    </label>
    <label>
        <input id="date" name="date" type="date" required/>
    </label>
    <label>
        <input id="time" name="time" type="time" required/>
    </label>
    <button type="submit">Create</button>
    <a class="a-button" href="#" hx-delete="/events/new?clubId=${clubId}" hx-target="#new-event-button" hx-swap="outerHTML">❌</a>
</form>