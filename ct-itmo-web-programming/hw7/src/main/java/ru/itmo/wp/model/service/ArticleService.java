package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.ArticleRepository;
import ru.itmo.wp.model.repository.impl.ArticleRepositoryImpl;

import java.util.List;

public class ArticleService {
    private final ArticleRepository articleRepository = new ArticleRepositoryImpl();

    public void validateArticle(User user, String title, String text) throws ValidationException {
        if (user == null) {
            throw new ValidationException("No user");
        }
        if (title == null || title.isBlank()) {
            throw new ValidationException("Title is empty");
        }
        if (text == null || text.isBlank()) {
            throw new ValidationException("Text is empty");
        }
        if (title.length() > 256) {
            throw new ValidationException("Title is too long");
        }
        if (text.length() > 16384) {
            throw new ValidationException("Text is too long");
        }

    }

    public void validateArticleId(String articleId) throws ValidationException {
        if (articleId == null || articleId.isBlank()) {
            throw new ValidationException("Can't convert articleId");
        }
        try {
            Long.parseLong(articleId);
        } catch (NumberFormatException ignore) {
            throw new ValidationException("Can't convert articleId");
        }
    }

    public void addArticle(User user, String title, String text) {
        Article article = new Article();
        article.setTitle(title);
        article.setText(text);
        article.setUserId(user.getId());
        articleRepository.save(article);
    }

    public List<Article> findAllNotHidden() {
        return articleRepository.findAllByHidden(false);
    }

    public Article findByIdAndUserId(long id, long userId) {
        return articleRepository.findByIdAndUserId(id, userId);
    }

    public List<Article> findAllByUserId(long userId) {
        return articleRepository.findAllByUserId(userId);
    }

    public void updateHidden(Article article, boolean hidden) {
        articleRepository.updateHidden(article, hidden);
    }
}
