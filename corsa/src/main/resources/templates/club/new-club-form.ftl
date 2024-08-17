<h3>New club</h3>
<form class="col gap16" hx-post="/clubs" hx-target="#main">
    <label class="row gap16">
        Name:
        <input type="text" name="name" placeholder="Club name" required>
    </label>
    <button type="submit">Add club</button>
</form>