package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.EventType;
import ru.itmo.wp.model.repository.EventRepository;
import ru.itmo.wp.model.repository.impl.EventRepositoryImpl;

public class EventService {
    private final EventRepository eventRepository = new EventRepositoryImpl();
    public Event getEvent(long userId, EventType type) {
        Event event = new Event();
        event.setUserId(userId);
        event.setType(type);
        return event;
    }
    public void save(Event event) {
        eventRepository.save(event);
    }
}
