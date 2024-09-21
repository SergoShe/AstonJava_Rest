package com.github.SergoShe.repository;

import com.github.SergoShe.model.Author;

import java.util.List;

public interface AuthorRepository extends Repository<Author, Long>{
    boolean existsByIds(List<Long> ids);
}
