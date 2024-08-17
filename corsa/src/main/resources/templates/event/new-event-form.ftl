<h3>New event</h3>
<form class="col gap16" hx-post="/events" hx-target="#main">
    <input value="${clubId}" name="clubId" type="hidden">
    <label>
        Name:
        <input name="name" type="text" placeholder="Event name" required>
    </label>
    <label>
        Date:
        <input name="date" type="date" required>
    </label>
    <button type="submit">Add event</button>
</form>