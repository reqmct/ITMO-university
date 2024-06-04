package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itmo.wp.form.UserDisabledCredentials;
import ru.itmo.wp.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UsersPage extends Page {
    private final UserService userService;

    public UsersPage(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "UsersPage";
    }

    @PostMapping("/setStatus")
    public String setStatus(@Valid UserDisabledCredentials userDisabledCredentials,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "UsersPage";
        }
        userService.updateDisabled(userDisabledCredentials.getUserId(), userDisabledCredentials.getDisabled());
        return "redirect:/users/all";
    }
}
