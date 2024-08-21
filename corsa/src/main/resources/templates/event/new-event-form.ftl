<h3>New event</h3>
<form class="col gap-16" hx-post="/events" hx-target="#main">
    <input value="${clubId}" name="clubId" type="hidden">
    <label class="row gap-8">
        Name:
        <input name="name" type="text" placeholder="Event name" required>
    </label>
    <label class="row gap-8">
        Date:
        <input name="date" type="text" placeholder="Event date" required>
    </label>
    <label class="row gap-8">
        Time:
        <input name="time" type="text" placeholder="Event time">
    </label>
    <div class="row gap-8">
        <button type="submit">Add event</button>
    </div>
</form>
