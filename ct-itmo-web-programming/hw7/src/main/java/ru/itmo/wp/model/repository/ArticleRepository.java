package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Article;

import java.util.List;

public interface ArticleRepository {
    void save(Article article);
    List<Article> findAllByHidden(boolean hidden);
    Article findByIdAndUserId(long id, long userId);
    List<Article> findAllByUserId(long userId);
    void updateHidden(Article article, boolean hidden);
}
