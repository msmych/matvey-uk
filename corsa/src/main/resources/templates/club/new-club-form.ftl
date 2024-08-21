<form class="col gap-32" hx-post="/clubs" hx-target="#page">
    <div class="t3">New club</div>
    <#import "../components/form-item.ftl" as formItem>
    <@formItem.item id="name" label="Name" name="name" placeholder="Dream Chasers"/>
    <div class="row gap-8">
        <button type="submit" class="primary">Add club</button>
    </div>
</form>
