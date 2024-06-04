package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class MyArticlesPage {
    private ArticleService articleService = new ArticleService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            request.getSession().setAttribute("message", "You should enter");
            throw new RedirectException("/index");
        }
        view.put("articles", articleService.findAllByUserId(user.getId()));
    }

    private void updHidden(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User user = (User) request.getSession().getAttribute("user");
        String stringArticleId = request.getParameter("articleId");
        String buttonText = request.getParameter("buttonText");
        articleService.validateArticleId(stringArticleId);
        long articleId = Long.parseLong(stringArticleId);
        Article article = articleService.findByIdAndUserId(articleId, user.getId());
        if ("Show".equals(buttonText)) {
            articleService.updateHidden(article, false);
        } else if("Hide".equals(buttonText)) {
            articleService.updateHidden(article, true);
        }
    }
}
