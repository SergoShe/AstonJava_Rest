package com.github.SergoShe.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    void save(T t);

    boolean update(T t);

    boolean deleteById(K id);

    Optional<T> findById(K id);

    List<T> findAll();

}