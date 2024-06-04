package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.exception.ValidationException;
import ru.itmo.wp.form.CommentCredentials;
import ru.itmo.wp.form.validator.CommentCredentialsValidator;
import ru.itmo.wp.service.CommentService;
import ru.itmo.wp.service.JwtService;
import ru.itmo.wp.service.PostService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1")
public class CommentController {
    private final JwtService jwtService;
    private final CommentService commentService;
    private final PostService postService;
    private final CommentCredentialsValidator commentCredentialsValidator;

    public CommentController(JwtService jwtService, CommentService commentService, PostService postService, CommentCredentialsValidator commentCredentialsValidator) {
        this.jwtService = jwtService;
        this.commentService = commentService;
        this.postService = postService;
        this.commentCredentialsValidator = commentCredentialsValidator;
    }

    @InitBinder("commentCredentials")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(commentCredentialsValidator);
    }

    @PostMapping("comments")
    public Post writeComment(@RequestParam String jwt,
                             @RequestBody @Valid CommentCredentials commentCredentials, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User user = jwtService.find(jwt);
        if (user == null) {
            bindingResult.addError(new ObjectError("no-user", "No such user"));
            throw new ValidationException(bindingResult);
        }
        commentService.save(commentCredentials, user);
        return postService.find(commentCredentials.getPost());
    }
}
