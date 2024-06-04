<#import "commons.ftlh" as c>

<@c.page>
    <div class="talks">
        <div class="form-box">
            <div class="header">Talk</div>
            <div class="body">
                <form action="" method="post">
                    <input type="hidden" name="action" value="talk"/>
                    <div class="field">
                        <div class="name">
                            <label for="target_user_id">Send Message</label>
                        </div>
                        <div class="value">
                            <select id="target_user_id" name="target_user_id">
                                <#list users as user>
                                    <option value="${user.id}">${user.login}</option>
                                </#list>
                            </select>
                        </div>
                    </div>
                    <div class="field">
                        <div class="name">
                            <label for="text">Text</label>
                        </div>
                        <div class="value">
                            <textarea id="text" name="text" type="text"></textarea>
                        </div>
                    </div>
                    <#if error??>
                        <div class="error">
                            ${error!}
                        </div>
                    </#if>
                    <div class="button-field">
                        <input type="submit" value="Send">
                    </div>
                </form>
            </div>
        </div>
        <div class="datatable">
            <div class="caption">Messages</div>
            <table>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Sender</th>
                    <th>Addressee</th>
                    <th>Message</th>
                    <th>DateTime</th>
                </tr>
                </thead>
                <tbody>
                <#list talks as talk>
                    <tr>
                        <td>${talk.id()}</td>
                        <td>${talk.sourceUser()}</td>
                        <td>${talk.targetUser()}</td>
                        <td>${talk.text()}</td>
                        <td>${talk.creationTime()}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>
</@c.page>