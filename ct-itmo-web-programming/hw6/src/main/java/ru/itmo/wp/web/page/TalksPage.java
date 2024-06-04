package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.dto.TalkDTO;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.TalkService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class TalksPage extends Page {
    private final TalkService talkService = new TalkService();

    @Override
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        User user = getUser();
        if (user == null) {
            setMessage("You are not registered.");
            throw new RedirectException("/index");
        }
    }

    private void talk(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User user = getUser();
        String targetUserId = request.getParameter("target_user_id");
        String text = request.getParameter("text");
        talkService.validateTalk(targetUserId, text);
        Talk talk = talkService.getTalk(user.getId(), Long.parseLong(targetUserId), text);
        talkService.save(talk);
    }

    @Override
    protected void after(HttpServletRequest request, Map<String, Object> view) {
        User user = getUser();
        view.put("users", userService.findAll());
        List<Talk> talks = talkService.findAllBySourceUserIdOrTargetUserId(user.getId(), user.getId());
        view.put("talks", talkService.getTalkDtoList(talks));
    }
}
