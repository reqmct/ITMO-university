<#-- @ftlvariable name="requestUri" type="java.lang.String" -->
<#macro page>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Codeforces</title>
        <link rel="stylesheet" href="/css/normalize.css">
        <link rel="stylesheet" href="/css/style.css">
        <link rel="icon" href="/favicon.ico">
    </head>
    <body>
    <@header/>
    <div class="middle">
        <@sidebar/>
        <main>
            <#nested/>
        </main>
    </div>
    <@footer/>
    </body>
    </html>
</#macro>

<#macro header>
    <header>
        <a href="#" class="logo"><img alt="Codeforces" title="Codeforces"
                                      src="/img/codeforces-logo-with-telegram.png"></a>
        <div class="languages">
            <a href="#"><img alt="In English" title="In English"
                             src="/img/gb.png"></a>
            <a href="#"><img alt="In Russian" title="In Russian"
                             src="/img/ru.png"></a>
        </div>
        <div class="enter-or-register-box">
            <#if user??>
                <@userlink user true/>
                |
                <a href="/logout">Logout</a>
            <#else>
                <a href="/enter">Enter</a>
                |
                <a href="/register">Register</a>
            </#if>
        </div>
        <nav>
            <#assign items=[
            {"href": "/index", "name": "Home"},
            {"href": "/misc/help", "name": "Help"},
            {"href": "/contests", "name": "Contests"},
            {"href": "/users", "name": "Users"}
            ]/>
            <ul>
                <#list items as item>
                    <li>
                        <#if !requestUri?has_content || requestUri != item.href>
                            <a href="${item.href}">${item.name}</a>
                        <#else>
                            <a href="${item.href}" class="text_with_underline">${item.name}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        </nav>
    </header>
</#macro>

<#macro sidebar>
    <aside>
        <#list posts?reverse as p>
            <@postSidebar p/>
        </#list>
    </aside>
</#macro>

<#macro footer>
    <footer>
        2010-2021 &copy; by Mike Mirzayanov
    </footer>
</#macro>

<#macro userlink user nameOnly=false>
<#-- @ftlvariable name="user" type="ru.itmo.web.hw4.model.User" -->
    <#if nameOnly>
        <a href="/user?handle=${user.handle}">${user.name}</a>
    <#else >
        <a href="/user?handle=${user.handle}" class="need_color" style="color: ${user.color}">${user.name}</a>
    </#if>
</#macro>

<#macro userlinkWithText user text>
<#-- @ftlvariable name="user" type="ru.itmo.web.hw4.model.User" -->
    <#if user?has_content>
        <a href="/user?handle=${user.handle}">${text}</a>
    <#else>
        <a>${text}</a>
    </#if>
</#macro>

<#function findBy items key value>
    <#list items as item>
        <#if item[key] == value>
            <#return item/>
        </#if>
    </#list>
</#function>

<#function scoreBy items key value>
    <#assign score = 0>
    <#list items as item>
        <#if item[key] == value>
            <#assign score = score + 1>
        </#if>
    </#list>
    <#return score/>
</#function>

<#macro shorten_text text isFull>
    <#if isFull || text?length &lt; 350>
        <p class="text">${text}</p>
    <#else>
        <p class="text">${text?substring(0, 350)}...</p>
    </#if>
</#macro>

<#macro post p isFull>
<#-- @ftlvariable name="p" type="ru.itmo.web.hw4.model.Post" -->

    <article>
        <#assign user = findBy(users, "id", p.userId)!/>
        <div class="title">${p.title}</div>
        <div class="information">By <@userlink user!/></div>
        <div class="body">
            <@shorten_text p.text isFull/>
        </div>
        <ul class="attachment">
            <li>Announcement of <a href="#">Bob ITMO</a></li>
            <li>Announcement of <a href="#">Bob ITMO</a></li>
        </ul>
        <div class="footer">
            <div class="left">
                <img src="img/voteup.png" title="Vote Up" alt="Vote Up"/>
                <span class="positive-score">+173</span>
                <img src="img/votedown.png" title="Vote Down" alt="Vote Down"/>
            </div>
            <div class="right">
                <img src="img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                2 days ago
                <img src="img/comments_16x16.png" title="Comments" alt="Comments"/>
                <a href="#">68</a>
            </div>
        </div>
    </article>
</#macro>

<#macro postSidebar post>
<#-- @ftlvariable name="post" type="ru.itmo.web.hw4.model.Post" -->
    <section>
        <div class="header">Post ${post.id}</div>
        <div class="body">
            <@shorten_text post.text false/>
        </div>
        <div class="footer">
            <a href="/post?post_id=${post.id}">View all</a>
        </div>
    </section>
</#macro>

