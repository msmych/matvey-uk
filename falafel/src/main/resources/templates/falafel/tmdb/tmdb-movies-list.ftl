<#import "tmdb-components.ftl" as tmdb/>

<#list movies as movie>
    <@tmdb.movie movie=movie />
</#list>
