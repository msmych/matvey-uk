<h3>New club</h3>
<form hx-post="/clubs" hx-target="#main">
    <label>
        Club name:
        <input type="text" name="name" placeholder="Club name" required>
    </label>
    <button type="submit">Add club</button>
</form>