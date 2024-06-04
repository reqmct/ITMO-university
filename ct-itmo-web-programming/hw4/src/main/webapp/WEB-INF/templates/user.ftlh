<#-- @ftlvariable name="user_id" type="java.lang.Long" -->
<#-- @ftlvariable name="handle" type="java.lang.String" -->
<#import "./common.ftlh" as c>

<@c.page>
    <head>
        <link rel="stylesheet" type="text/css" href="/css/user.css">
    </head>
    <#if user_id?has_content && user_id?is_number>
        <#assign u = c.findBy(users, "id", user_id)!/>
    <#elseif handle?has_content>
        <#assign u = c.findBy(users, "handle", handle)!/>
    </#if>
    <div class="user_elements">
        <#if u?has_content>
            <#assign postScore = c.scoreBy(posts, "user_id", u.id) !/>
            <#assign userIndex = users?seq_index_of(u)!/>
            <div class="user_page">
                <div class="link_to_user">
                    <@c.userlink u/>
                </div>
                <#assign items=[
                {"value": "id: ${u.id}"},
                {"value": "handle: ${u.handle}"},
                {"value": "name: ${u.name}"},
                {"value": "score of posts: ${postScore}", "href": "/posts?user_id=${u.id}"}
                ]/>
                <ul>
                    <#list items as item>
                        <li>
                            <#if item.href?has_content>
                                <a href=${item.href}>
                                    ${item.value}
                                </a>
                            <#else>
                                <a>
                                    ${item.value}
                                </a>
                            </#if>
                        </li>
                    </#list>
                </ul>
            </div>
            <div class="steps">
                <#assign leftUser = users[userIndex - 1]!/>
                <#assign rightUser = users[userIndex + 1]!/>
                <@c.userlinkWithText leftUser "←"/>
                <@c.userlinkWithText rightUser "→"/>
            </div>
        <#else>
            No such user
        </#if>
    </div>
</@c.page>
