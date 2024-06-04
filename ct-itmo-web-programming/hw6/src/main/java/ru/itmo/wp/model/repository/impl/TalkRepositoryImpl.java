package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.repository.AbstractBasicRepository;
import ru.itmo.wp.model.repository.TalkRepository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class TalkRepositoryImpl extends AbstractBasicRepository<Talk> implements TalkRepository {

    @Override
    public void save(Talk talk) {
        String saveFields = "`sourceUserid`, `targetUserid`, `text`, `creationTime`";
        String saveValues = "?, ?, ?, NOW()";
        super.save(talk, saveFields, saveValues, talk.getSourceUserId(), talk.getTargetUserId(), talk.getText());
    }

    @Override
    public List<Talk> findAllBySourceUserIdOrTargetUserId(long sourceUserId, long targetUserId) {
        return super.findAllBy("WHERE sourceUserId=? OR targetUserId=? ORDER BY creationTime DESC",
                sourceUserId, targetUserId);
    }


    @Override
    protected Talk toEntity(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Talk talk = new Talk();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id" -> talk.setId(resultSet.getLong(i));
                case "sourceUserId" -> talk.setSourceUserId(resultSet.getLong(i));
                case "targetUserId" -> talk.setTargetUserId(resultSet.getLong(i));
                case "text" -> talk.setText(resultSet.getString(i));
                case "creationTime" -> talk.setCreationTime(resultSet.getTimestamp(i));
                default -> {
                }
                // No operations.
            }
        }

        return talk;
    }

    @Override
    protected String getTableName() {
        return "Talk";
    }
}
