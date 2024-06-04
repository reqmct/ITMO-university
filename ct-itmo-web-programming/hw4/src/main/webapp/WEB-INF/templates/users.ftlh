<#import "./common.ftlh" as c>

<@c.page>
    <head>
        <link rel="stylesheet" type="text/css" href="/css/users.css">
    </head>
    <div class="datatable">
        <div class="caption">Submissions</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>User</th>
                <th>Handle</th>
            </tr>
            </thead>
            <tbody>
            <#list users as u>
                <tr>
                    <td>${u.id}</td>
                    <td><@c.userlink u/></td>
                    <td>${u.handle}</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</@c.page>
