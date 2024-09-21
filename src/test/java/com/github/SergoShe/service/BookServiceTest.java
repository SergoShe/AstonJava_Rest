package com.github.SergoShe.service;

import com.github.SergoShe.DTO.AuthorDTO;
import com.github.SergoShe.DTO.BookDTO;
import com.github.SergoShe.mapper.BookMapper;
import com.github.SergoShe.model.Book;
import com.github.SergoShe.repository.AuthorRepository;
import com.github.SergoShe.repository.BookRepository;
import com.github.SergoShe.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(bookRepository, authorRepository);
    }

    @Test
    void testCreateSuccess() {
        // Given
        AuthorDTO authorDTO = new AuthorDTO(1L, "firstName", "surName", null);
        BookDTO bookDTO = new BookDTO(null, "Title", 2000, Collections.singletonList(authorDTO), null);

        when(authorRepository.existsByIds(anyList())).thenReturn(true);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setBookId(1L);
            return book;
        });

        // When
        BookDTO createdBookDTO = bookService.create(bookDTO);

        // Then
        verify(bookRepository).save(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();

        assertNotNull(createdBookDTO.getBookId());
        assertEquals(bookDTO.getTitle(), capturedBook.getTitle());
        assertEquals(bookDTO.getYear(), capturedBook.getYear());
    }

    @Test
    void testCreateNoAuthors() {
        BookDTO bookDTO = new BookDTO(null, "Title", 2000, null, null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookService.create(bookDTO));
        assertEquals("A book must have at least one author", exception.getMessage());
    }

    @Test
    void testCreateAuthorNotExist() {
        AuthorDTO authorDTO = new AuthorDTO(1L, "firstName", "surName", null);
        BookDTO bookDTO = new BookDTO(null, "Title", 2000, Collections.singletonList(authorDTO), null);
        when(authorRepository.existsByIds(anyList())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookService.create(bookDTO));
        assertEquals("One or more authors is not exist", exception.getMessage());
    }

    @Test
    void testGetByIdSuccess() {
        long bookId = 1L;
        Book book = new Book(bookId, "Title", 2000, Collections.emptyList(), null);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookDTO bookDTO = bookService.getById(bookId);

        assertEquals(bookId, bookDTO.getBookId());
        assertEquals(book.getTitle(), bookDTO.getTitle());
    }

    @Test
    void testGetByIdNotFound() {
        long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.getById(bookId));
        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void testGetAll() {
        Book book = new Book(1L, "Title", 2000, Collections.emptyList(), null);
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));

        List<BookDTO> bookDTOS = bookService.getAll();

        assertEquals(1, bookDTOS.size());
        assertEquals(book.getTitle(), bookDTOS.get(0).getTitle());
    }

    @Test
    void testUpdateSuccess() {
        BookDTO bookDTO = new BookDTO(null, "Title", 2000, null, null);

        when(bookRepository.update(any(Book.class))).thenReturn(true);

        boolean result = bookService.update(bookDTO);

        assertTrue(result);
        verify(bookRepository).update(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        assertEquals(bookDTO.getTitle(),capturedBook.getTitle());
    }

    @Test
    void testUpdateNotFound(){
        BookDTO bookDTO = new BookDTO(null, "Title", 2000, null, null);

        when(bookRepository.update(any(Book.class))).thenReturn(false);

        boolean result = bookService.update(bookDTO);

        assertFalse(result);
    }

    @Test
    void testDeleteSuccess(){
        long bookId = 1L;
        when(bookRepository.deleteById(bookId)).thenReturn(true);

        boolean result = bookService.delete(bookId);

        assertTrue(result);
    }

    @Test
    void testDeleteNotFound(){
        long bookId = 1L;
        when(bookRepository.deleteById(bookId)).thenReturn(false);

        boolean result = bookService.delete(bookId);

        assertFalse(result);
    }
}
