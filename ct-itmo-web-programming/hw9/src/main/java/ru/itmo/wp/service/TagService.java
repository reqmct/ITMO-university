package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.itmo.wp.domain.Tag;
import ru.itmo.wp.repository.TagRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public boolean hasErrors(Tag tag) {
        DataBinder binder = new DataBinder(tag);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        binder.setValidator(validator);
        binder.validate();
        BindingResult bindingResult = binder.getBindingResult();
        return bindingResult.hasErrors();
    }

    public void saveTags(Set<Tag> tags) {
        Set<String> dbNameTags = new HashSet<>(tagRepository.findAllNames());
        for (Tag tag : tags) {
            if (!dbNameTags.contains(tag.getName())) {
                tagRepository.save(tag);
            } else {
                Tag realTag = tagRepository.findTagByName(tag.getName());
                tag.setId(realTag.getId());
                tag.setCreationTime(realTag.getCreationTime());
            }
        }
    }
}
