package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.dto.TalkDTO;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.TalkRepository;
import ru.itmo.wp.model.repository.impl.TalkRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class TalkService {
    private final UserService userService = new UserService();

    private final TalkRepository talkRepository = new TalkRepositoryImpl();

    public TalkDTO getTalkDTO(Talk talk) {
        User sourceUser = userService.find(talk.getSourceUserId());
        User targetUser = userService.find(talk.getTargetUserId());
        return new TalkDTO(talk.getId(), sourceUser.getLogin(), targetUser.getLogin(),
                talk.getText(), talk.getCreationTime());
    }

    public List<TalkDTO> getTalkDtoList(List<Talk> list) {
        List<TalkDTO> listDTO = new ArrayList<>();
        for (Talk talk : list) {
            listDTO.add(getTalkDTO(talk));
        }
        return listDTO;
    }

    public void validateTalk(String stringTargetUserId, String text) throws ValidationException {
        if (Strings.isNullOrEmpty(text)) {
            throw new ValidationException("Text is required");
        }
        if (text.isBlank()) {
            throw new ValidationException("Text is required");
        }
        if (text.length() > 16384) {
            throw new ValidationException("Text is too long");
        }
        try {
            long id = Long.parseLong(stringTargetUserId);
            if (userService.find(id) == null) {
                throw new ValidationException("Can't find target user");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Target User id it's not a number");
        }
    }

    public Talk getTalk(long sourceUserId, long targetUserId, String text) {
        Talk talk = new Talk();
        talk.setSourceUserId(sourceUserId);
        talk.setTargetUserId(targetUserId);
        talk.setText(text);
        return talk;
    }

    public void save(Talk talk) {
        talkRepository.save(talk);
    }

    public List<Talk> findAllBySourceUserIdOrTargetUserId(long sourceUserId, long targetUserId) {
        return talkRepository.findAllBySourceUserIdOrTargetUserId(sourceUserId, targetUserId);
    }
}
