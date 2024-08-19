<h3>New event</h3>
<form class="col gap-16" hx-post="/events" hx-target="#main">
    <input value="${clubId}" name="clubId" type="hidden">
    <label class="row gap-8">
        Name:
        <input name="name" type="text" placeholder="Event name" required>
    </label>
    <label class="row gap-8">
        Date:
        <input name="date" type="date" required>
    </label>
    <label class="row gap-8">
        Time:
        <input name="time" type="time">
    </label>
    <button type="submit">Add event</button>
</form>