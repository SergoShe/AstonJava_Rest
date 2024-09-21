package com.github.SergoShe.service;

import java.util.List;

public interface Service<T,K> {

    T create(T t) ;

    List<T> getAll();

    T getById(K id);

    boolean update(T t);

    boolean delete(K id);
}
