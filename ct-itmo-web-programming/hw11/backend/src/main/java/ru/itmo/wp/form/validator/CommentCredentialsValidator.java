package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.CommentCredentials;
import ru.itmo.wp.form.UserCredentials;
import ru.itmo.wp.service.PostService;


@Component
public class CommentCredentialsValidator implements Validator {
    private final PostService postService;

    public CommentCredentialsValidator(PostService postService) {
        this.postService = postService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CommentCredentials.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            CommentCredentials comment = (CommentCredentials) target;
            if (postService.find(comment.getPost()) == null) {
                errors.reject("invalid-post", "Invalid post");
            }
        }
    }
}
