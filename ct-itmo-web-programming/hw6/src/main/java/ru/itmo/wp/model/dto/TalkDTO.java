package ru.itmo.wp.model.dto;

import java.util.Date;

public record TalkDTO(long id, String sourceUser, String targetUser, String text, Date creationTime) {
}