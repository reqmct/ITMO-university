package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.repository.AbstractBasicRepository;
import ru.itmo.wp.model.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("SqlNoDataSourceInspection")
public class UserRepositoryImpl extends AbstractBasicRepository<User> implements UserRepository {

    @Override
    public User find(long id) {
        return super.find(id);
    }

    @Override
    public User findByLogin(String login) {
        return super.findBy("WHERE login=?", login);
    }

    @Override
    public User findByLoginOrEmailAndPasswordSha(String loginOrEmail, String passwordSha) {
        return super.findBy("WHERE (login=? OR email=?) AND passwordSha=?",
                loginOrEmail, loginOrEmail, passwordSha);
    }

    @Override
    public User findByEmail(String email) {
        return super.findBy("WHERE email=?", email);
    }

    @Override
    public List<User> findAll() {
        return super.findAll();
    }

    @Override
    public int findCount() {
        return super.findCount();
    }

    @Override
    public void save(User user, String passwordSha) {
        String saveFields = "`login`, `passwordSha`, `creationTime`, `email`";
        String saveValues = "?, ?, NOW(), ?";
        super.save(user, saveFields, saveValues, user.getLogin(), passwordSha, user.getEmail());
    }

    @Override
    protected User toEntity(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        User user = new User();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id" -> user.setId(resultSet.getLong(i));
                case "login" -> user.setLogin(resultSet.getString(i));
                case "creationTime" -> user.setCreationTime(resultSet.getTimestamp(i));
                case "email" -> user.setEmail(resultSet.getString(i));
                default -> {
                }
                // No operations.
            }
        }

        return user;
    }

    @Override
    protected String getTableName() {
        return "User";
    }

}
