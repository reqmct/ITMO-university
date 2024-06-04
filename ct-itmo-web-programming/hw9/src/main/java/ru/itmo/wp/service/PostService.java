package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.Tag;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.PostCredentials;
import ru.itmo.wp.repository.PostRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagService tagService;

    public PostService(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    public void writeComment(Comment comment, Post post, User user) {
        post.addComment(comment);
        comment.setPost(post);
        comment.setUser(user);
        postRepository.save(post);
    }

    public Post toPost(PostCredentials postForm) {
        Post post = new Post();
        post.setTitle(postForm.getTitle());
        post.setText(postForm.getText());
        Set<String> allTags = new HashSet<>(Arrays.asList(postForm.getTags().split("\\s+")));
        Set<Tag> tagsSet = new HashSet<>();
        for (String tag : allTags) {
            Tag tmp = new Tag();
            tmp.setName(tag);
            if (!tagsSet.contains(tmp) && !tagService.hasErrors(tmp)) {
                tagsSet.add(tmp);
            }
        }
        tagService.saveTags(tagsSet);
        post.setTags(tagsSet);
        return post;
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreationTimeDesc();
    }

    public Post findById(Long id) {
        return id == null ? null : postRepository.findById(id).orElse(null);
    }
}
