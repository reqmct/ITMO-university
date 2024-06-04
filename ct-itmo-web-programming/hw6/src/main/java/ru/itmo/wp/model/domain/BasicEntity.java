package ru.itmo.wp.model.domain;

import java.util.Date;

public class BasicEntity {
    protected long id;
    protected Date creationTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
