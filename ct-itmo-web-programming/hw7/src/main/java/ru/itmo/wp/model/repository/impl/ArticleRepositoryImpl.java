package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.repository.AbstractBasicRepository;
import ru.itmo.wp.model.repository.ArticleRepository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class ArticleRepositoryImpl extends AbstractBasicRepository<Article> implements ArticleRepository {


    @Override
    public void save(Article article) {
        String saveFields = "`userId`, `title`, `text`, `creationTime`, `hidden`";
        String saveValues = "?, ?, ?, NOW(), ?";
        super.save(article, saveFields, saveValues,
                article.getUserId(), article.getTitle(), article.getText(), article.isHidden());
    }

    @Override
    public List<Article> findAllByHidden(boolean hidden) {

        return super.findAllBy("WHERE hidden=? ORDER BY creationTime DESC", hidden);
    }


    @Override
    public List<Article> findAllByUserId(long userId) {
        return super.findAllBy("WHERE userId=?", userId);
    }

    @Override
    public void updateHidden(Article article, boolean hidden) {
        super.update("SET hidden=? WHERE id=?", hidden, article.getId());
    }

    @Override
    public Article findByIdAndUserId(long id, long userId) {
        return super.findBy("WHERE id=? AND userId=?", id, userId);
    }

    @Override
    protected Article toEntity(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Article article = new Article();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id" -> article.setId(resultSet.getLong(i));
                case "userId" -> article.setUserId(resultSet.getLong(i));
                case "title" -> article.setTitle(resultSet.getString(i));
                case "text" -> article.setText(resultSet.getString(i));
                case "creationTime" -> article.setCreationTime(resultSet.getTimestamp(i));
                case "hidden" -> article.setHidden(resultSet.getBoolean(i));
                default -> {
                }
                // No operations.
            }
        }

        return article;
    }


    @Override
    protected String getTableName() {
        return "Article";
    }
}
