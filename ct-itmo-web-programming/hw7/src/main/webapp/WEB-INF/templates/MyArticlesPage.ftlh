<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="my_articles datatable">
        <div class="caption">Articles</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th>Button</th>
            </tr>
            </thead>
            <tbody>
            <#list articles as article>
                <tr>
                    <td class="id"> ${article.id} </td>
                    <td class="title"> ${article.title} </td>
                    <td class="hidden">
                        <button data-articleId=${article.id}><#if article.hidden>Show<#else>Hide</#if></button>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>

    <script>
        $(function () {
            $(".hidden button").click(function () {
                const $error = $(this).find(".error");
                const $this = $(this);

                ajax(
                    {
                        action: "updHidden",
                        articleId: $(this).attr("data-articleId"),
                        buttonText: $this.text()
                    },
                    $error,
                    function (response) {
                        if ($this.text() === "Hide") {
                            $this.text("Show");
                        } else if($this.text() === "Show") {
                            $this.text("Hide");
                        }
                    }
                );

                return false;
            });
        })
    </script>

</@c.page>
