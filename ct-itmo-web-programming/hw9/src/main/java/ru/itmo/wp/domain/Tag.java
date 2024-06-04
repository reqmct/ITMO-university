package ru.itmo.wp.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Entity
@Table(
        indexes = {@Index(columnList = "creationTime"),
                @Index(columnList = "name", unique = true)}
)

public class Tag {
    @Id
    @GeneratedValue
    private long id;
    @NotNull
    @NotBlank
    @Size(min=1, max=64)
    @Pattern(regexp = "[a-z]+", message = "Only small latin characters")
    private String name;
    @CreationTimestamp
    private Date creationTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

}
