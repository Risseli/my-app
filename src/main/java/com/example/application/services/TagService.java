package com.example.application.services;

import com.example.application.data.TagRepository;
import com.example.application.data.Tag;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class TagService {

    private final TagRepository tagRepository;
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }
}