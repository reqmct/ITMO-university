package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ArticlePage {
    private ArticleService articleService = new ArticleService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        if (request.getSession().getAttribute("user") == null) {
            request.getSession().setAttribute("message", "You should enter");
            throw new RedirectException("/index");
        }
    }

    private void add(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User user = (User) request.getSession().getAttribute("user");
        String title = request.getParameter("title");
        String text = request.getParameter("text");
        articleService.validateArticle(user, title, text);
        articleService.addArticle(user, title, text);
        view.put("message", "Article created");
    }
}
