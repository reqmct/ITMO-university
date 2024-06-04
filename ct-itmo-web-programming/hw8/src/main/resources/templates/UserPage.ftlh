<#-- @ftlvariable name="enterForm" type="ru.itmo.wp.form.UserCredentials" -->
<#import "/spring.ftl" as spring>
<#import "commons.ftlh" as c>

<@c.page>
    <#if user??>
        <ul>
            <li>Id: ${user.id}</li>
            <li>Login: ${user.login}</li>
            <li>Creation time: ${user.creationTime}</li>
        </ul>
    <#else>
        No such user
    </#if>
</@c.page>
