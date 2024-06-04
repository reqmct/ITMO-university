<#import "commons.ftlh" as c>
<#import "/spring.ftl" as spring>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th>Created</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <#if users?size==0>
                <tr class="noData">
                    <td colspan="3">
                        No data
                    </td>
                </tr>
            <#else>
                <#list users as user>
                    <tr>
                        <td class="id">${user.id}</td>
                        <td class="login">${user.login}</td>
                        <td class="creationTime">${user.creationTime}</td>
                        <td class="disabled">
                            <form method="post" action="<@spring.url '/users/setStatus'/>">
                                <input type="hidden" name="userId" value="${user.id}">
                                <input type="hidden" name="disabled" value=${user.disabled?string("enable", "disable")}>
                                <button type="submit">${user.disabled?string("enable", "disable")}</button>
                            </form>
                        </td>
                    </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>
</@c.page>
