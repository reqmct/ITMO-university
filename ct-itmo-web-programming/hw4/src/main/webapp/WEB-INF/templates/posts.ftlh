<#import "./common.ftlh" as c>

<@c.page>
    <head>
        <link rel="stylesheet" type="text/css" href="/css/posts.css">
    </head>
    <ul>
        <#list posts?reverse as p>
            <#if p.user_id == user_id>
                <li class="post_element">
                    <@c.post p false/>
                </li>
            </#if>
        </#list>
    </ul>
</@c.page>