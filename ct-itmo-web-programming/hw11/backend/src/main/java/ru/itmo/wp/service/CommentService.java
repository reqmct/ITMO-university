package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.CommentCredentials;
import ru.itmo.wp.repository.CommentRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void save(CommentCredentials commentCredentials, User user) {
        Comment comment = new Comment();
        comment.setPost(commentCredentials.getPost());
        comment.setText(commentCredentials.getText());
        comment.setUser(user);
        commentRepository.save(comment);
    }

}
