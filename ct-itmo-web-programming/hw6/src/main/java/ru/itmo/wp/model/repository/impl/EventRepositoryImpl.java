package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.EventType;
import ru.itmo.wp.model.repository.AbstractBasicRepository;
import ru.itmo.wp.model.repository.EventRepository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class EventRepositoryImpl extends AbstractBasicRepository<Event> implements EventRepository {

    @Override
    public void save(Event event) {
        String saveFields = "`userId`, `type`, `creationTime`";
        String saveValues = "?, ?, NOW()";
        super.save(event, saveFields, saveValues, event.getUserId(), event.getType());
    }


    @Override
    protected Event toEntity(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Event event = new Event();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id":
                    event.setId(resultSet.getLong(i));
                    break;
                case "userId":
                    event.setUserId(resultSet.getLong(i));
                    break;
                case "creationTime":
                    event.setCreationTime(resultSet.getTimestamp(i));
                    break;
                case "type":
                    event.setType(EventType.valueOf(resultSet.getString(i)));
                    break;
                default:
                    // No operations.
            }
        }

        return event;
    }

    @Override
    protected String getTableName() {
        return "Event";
    }
}
