package com.github.SergoShe.service.impl;

import com.github.SergoShe.DTO.AuthorDTO;
import com.github.SergoShe.DTO.BookDTO;
import com.github.SergoShe.mapper.BookMapper;
import com.github.SergoShe.model.Book;
import com.github.SergoShe.repository.AuthorRepository;
import com.github.SergoShe.repository.BookRepository;
import com.github.SergoShe.repository.impl.AuthorRepositoryImpl;
import com.github.SergoShe.repository.impl.BookRepositoryImpl;
import com.github.SergoShe.service.BookService;

import java.util.List;
import java.util.stream.Collectors;

public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;
    private static BookServiceImpl bookServiceImpl;

    public BookServiceImpl() {
        this.bookRepository = BookRepositoryImpl.getInstance();
        this.authorRepository = AuthorRepositoryImpl.getInstance();
        this.bookMapper = BookMapper.INSTANCE;
    }

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = BookMapper.INSTANCE;
    }

    public static BookServiceImpl getInstance() {
        if (bookServiceImpl == null) {
            bookServiceImpl = new BookServiceImpl();
        }
        return bookServiceImpl;
    }

    @Override
    public BookDTO create(BookDTO bookDTO) {
        if (bookDTO.getAuthors() == null || bookDTO.getAuthors().isEmpty()) {
            throw new IllegalArgumentException("A book must have at least one author");
        }
        List<Long> authorIds = bookDTO.getAuthors().stream().map(AuthorDTO::getAuthorId).collect(Collectors.toList());
        if (!authorRepository.existsByIds(authorIds)) {
            throw new IllegalArgumentException("One or more authors is not exist");
        }
        Book book = bookMapper.bookDTOToBook(bookDTO);
        return bookMapper.bookToBookDTO(bookRepository.save(book));
    }

    @Override
    public BookDTO getById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Book not found"));
        return bookMapper.bookToBookDTO(book);
    }

    @Override
    public List<BookDTO> getAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::bookToBookDTO).collect(Collectors.toList());
    }

    @Override
    public boolean update(BookDTO bookDTO) {
        Book book = bookMapper.bookDTOToBook(bookDTO);
        return bookRepository.update(book);
    }

    @Override
    public boolean delete(Long id) {
        return bookRepository.deleteById(id);
    }
}