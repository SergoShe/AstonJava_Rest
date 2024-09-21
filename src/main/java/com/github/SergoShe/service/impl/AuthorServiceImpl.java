package com.github.SergoShe.service.impl;

import com.github.SergoShe.DTO.AuthorDTO;
import com.github.SergoShe.mapper.AuthorMapper;
import com.github.SergoShe.model.Author;
import com.github.SergoShe.repository.AuthorRepository;
import com.github.SergoShe.repository.impl.AuthorRepositoryImpl;
import com.github.SergoShe.service.AuthorService;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private static AuthorServiceImpl authorServiceImpl;

    public AuthorServiceImpl() {
        this.authorRepository = AuthorRepositoryImpl.getInstance();
        this.authorMapper = AuthorMapper.INSTANCE;
    }

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
        this.authorMapper = AuthorMapper.INSTANCE;
    }

    public static AuthorServiceImpl getInstance() {
        if (authorServiceImpl == null) {
            authorServiceImpl = new AuthorServiceImpl();
        }
        return authorServiceImpl;
    }

    @Override
    public AuthorDTO create(AuthorDTO authorDTO) {
        Author author = authorMapper.authorDTOToAuthor(authorDTO);
        authorRepository.save(author);
        return authorMapper.authorToAuthorDTO(author);
    }

    @Override
    public AuthorDTO getById(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Author not found"));
        return authorMapper.authorToAuthorDTO(author);
    }

    @Override
    public List<AuthorDTO> getAll() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream().map(authorMapper::authorToAuthorDTO).collect(Collectors.toList());
    }

    @Override
    public boolean update(AuthorDTO authorDTO) {
        Author author = authorMapper.authorDTOToAuthor(authorDTO);
        return authorRepository.update(author);
    }

    @Override
    public boolean delete(Long id) {
        return authorRepository.deleteById(id);
    }
}