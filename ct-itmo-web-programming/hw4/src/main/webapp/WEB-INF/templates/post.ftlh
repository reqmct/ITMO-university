<#-- @ftlvariable name="user_id" type="java.lang.Long" -->
<#-- @ftlvariable name="handle" type="java.lang.String" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#assign post = c.findBy(posts, "id", post_id)/>
    <@c.post post true/>
</@c.page>
