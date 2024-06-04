<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <#if user?? && user.admin>
                    <th>Admin</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <template>
                <tr>
                    <td class="user-id"></td>
                    <td class="user_login"></td>
                    <#if user?? && user.admin>
                        <td class="admin">
                            <button></button>
                        </td>
                    </#if>
                </tr>
            </template>
            <tr class="noData">
                <td colspan="3">
                    No data
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <script>
        $(function () {
            const $noData = $(".users .noData");
            $noData.find("td").text("Loading...");

            $.ajax({
                dataType: "json",
                data: {
                    action: "findAll"
                },
                success: function (response) {
                    response["users"].forEach(function (user) {
                        $noData.hide();
                        const $tr = $($(".users template").prop("content")).clone();
                        $tr.find(".user-id").text(user["id"]);
                        $tr.find(".user_login").text(user["login"]);
                        if (user["admin"]) {
                            $tr.find(".admin button").text("disable");
                        } else {
                            $tr.find(".admin button").text("enable");
                        }
                        $tr.find("tr").attr("data-userId", user["id"]);
                        $tr.find("button").attr("data-userId", user["id"]);
                        $(".users tbody").append($tr);
                    });
                    $(".users button").click(function () {
                        const $this = $(this);
                        const $error = $(this).find(".error");
                        ajax(
                            {
                                action: "updAdmin",
                                userId: $this.attr("data-userId"),
                                buttonText: $this.text()
                            },
                            $error,
                            function (response) {
                                if ($this.text() === "enable") {
                                    $this.text("disable");
                                } else if ($this.text() === "disable") {
                                    $this.text("enable");
                                }
                            }
                        )
                    });
                },
                type: "POST"
            });
        })
    </script>
</@c.page>
