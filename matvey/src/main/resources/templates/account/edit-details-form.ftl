<form class="col gap-16"
      hx-patch="/accounts/${id}"
      hx-target="#content">
    <div class="t1">Update details</div>
    <label class="row gap-8">
        <span>Name:</span>
        <input name="name" value="${name}">
    </label>
    <button type="submit" class="primary">Save</button>
</form>
