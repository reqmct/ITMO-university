package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @noinspection unused
 */
public class UsersPage {
    private final UserService userService = new UserService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            request.getSession().setAttribute("user", userService.find(user.getId()));
        }
    }

    private void updAdmin(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User user = (User) request.getSession().getAttribute("user");
        User currUser = userService.find(user.getId());
        String stringId = request.getParameter("userId");
        String text = request.getParameter("buttonText");
        if (currUser.isAdmin()) {
            userService.validateId(stringId);
            long id = Long.parseLong(stringId);
            if ("enable".equals(text)) {
                userService.updateAdmin(id, true);
                if (id == currUser.getId()) {
                    currUser.setAdmin(true);
                }
            } else if ("disable".equals(text)) {
                userService.updateAdmin(id, false);
                if (id == currUser.getId()) {
                    currUser.setAdmin(false);
                }
            }
        }
        request.getSession().setAttribute("user", currUser);
        if (!currUser.isAdmin()) {
            request.getSession().setAttribute("message", "You are not admin");
            throw new RedirectException("/index");
        }
    }

    private void findAll(HttpServletRequest request, Map<String, Object> view) {
        view.put("users", userService.findAll());
    }

    private void findUser(HttpServletRequest request, Map<String, Object> view) {
        view.put("user",
                userService.find(Long.parseLong(request.getParameter("userId"))));
    }
}
