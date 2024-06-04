package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.Role;
import ru.itmo.wp.security.AnyRole;
import ru.itmo.wp.security.Authorized;
import ru.itmo.wp.security.Guest;
import ru.itmo.wp.service.PostService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class PostPage extends Page {
    private final PostService postService;

    public PostPage(PostService postService) {
        this.postService = postService;
    }

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @GetMapping("/post/{id}")
    @Guest
    private String getPost(@PathVariable String id, Model model) {
        Post post = postService.findById(parseId(id));
        model.addAttribute("post", post);
        model.addAttribute("comment", new Comment());
        return "PostPage";
    }
    @PostMapping("/post/{id}")
    @Authorized
    private String setComment(@PathVariable String id,
                              @Valid @ModelAttribute("comment") Comment comment,
                              BindingResult bindingResult,
                              Model model,
                              HttpSession httpSession) {
        Post post = postService.findById(parseId(id));
        model.addAttribute("post", post);
        if (bindingResult.hasErrors()) {
            return "PostPage";
        }
        postService.writeComment(comment, post, super.getUser(httpSession));
        return "redirect:/post/{id}";
    }
}

