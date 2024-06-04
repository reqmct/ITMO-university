<#-- @ftlvariable name="posts" type="ru.itmo.wp.domain.Post[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <#if post??>
        <@c.post post=post/>
        <#if user??>
            <div class="comment form">
                <div class="header">Comment</div>
                <div class="body">
                    <form method="post" action="">
                        <div class="field">
                            <label>
                                <div class="value">
                                    <textarea name="text" value="${comment.text!}"></textarea>
                                </div>
                            </label>
                            <@c.error "comment.text"/>
                        </div>
                        <div class="button-field">
                            <input type="submit" value="Comment">
                        </div>
                    </form>
                </div>
            </div>
        </#if>
        <#if post.comments?has_content>
            <table class="comment-table">
                <#list post.comments as comment>
                    <tr class="comment">
                        <td class="login">${comment.user.login}:</td>
                        <td class="text">${comment.text}</td>
                    </tr>
                </#list>
            </table>
        </#if>
    <#else>
        Post not found
    </#if>
</@c.page>
