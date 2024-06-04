package ru.itmo.wp.form;

import ru.itmo.wp.domain.Post;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CommentCredentials {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 65000)
    private String text;

    @NotNull
    private Post post;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
