package com.github.SergoShe.service;

import com.github.SergoShe.DTO.AuthorDTO;
import com.github.SergoShe.DTO.BookDTO;
import com.github.SergoShe.model.Author;
import com.github.SergoShe.repository.AuthorRepository;
import com.github.SergoShe.service.impl.AuthorServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Mock
    private AuthorRepository authorRepository;

    @Captor
    private ArgumentCaptor<Author> authorCaptor;

    @BeforeEach
    public void setUp() {
        authorService = new AuthorServiceImpl(authorRepository);
    }

    @Test
    void testCreateSuccess(){
        BookDTO bookDTO = new BookDTO(1L, "Title", 2020, null, null);
        AuthorDTO authorDTO = new AuthorDTO(1L, "firstName", "surName", Collections.singletonList(bookDTO));

        when(authorRepository.save(any(Author.class))).thenAnswer(invocation ->{
            Author author = invocation.getArgument(0);
            author.setAuthorId(1L);
            return author;
        });

        AuthorDTO createdAuthorDTO = authorService.create(authorDTO);

        verify(authorRepository).save(authorCaptor.capture());
        Author capturedAuthor = authorCaptor.getValue();

        assertNotNull(createdAuthorDTO.getAuthorId());
        assertEquals(authorDTO.getFirstName(), capturedAuthor.getFirstName());
        assertEquals(authorDTO.getLastName(), capturedAuthor.getLastName());
    }

    @Test
    void testGetByIdSuccess(){
        long authorId = 1L;
        Author author = new Author(authorId, "firstName", "surName", Collections.emptyList());
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        AuthorDTO authorDTO = authorService.getById(authorId);

        assertEquals(authorId, authorDTO.getAuthorId());
        assertEquals(author.getFirstName(), authorDTO.getFirstName());
    }

    @Test
    void testGetByIdNotFound() {
        long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authorService.getById(authorId));
        assertEquals("Author not found", exception.getMessage());
    }

    @Test
    void testGetAll() {
        Author author = new Author(1L, "firstName", "surName", Collections.emptyList());
        when(authorRepository.findAll()).thenReturn(Collections.singletonList(author));

        List<AuthorDTO> authorDTOS = authorService.getAll();

        assertEquals(1, authorDTOS.size());
        assertEquals(author.getFirstName(), authorDTOS.get(0).getFirstName());
    }

    @Test
    void testUpdateSuccess() {
        AuthorDTO authorDTO = new AuthorDTO(1L, "firstName", "surName", null);

        when(authorRepository.update(any(Author.class))).thenReturn(true);

        boolean result = authorService.update(authorDTO);

        assertTrue(result);
        verify(authorRepository).update(authorCaptor.capture());
        Author capturedAuthor = authorCaptor.getValue();
        assertEquals(authorDTO.getFirstName(),capturedAuthor.getFirstName());
    }

    @Test
    void testUpdateNotFound(){
        AuthorDTO authorDTO = new AuthorDTO(1L, "firstName", "surName", null);

        when(authorRepository.update(any(Author.class))).thenReturn(false);

        boolean result = authorService.update(authorDTO);

        assertFalse(result);
    }

    @Test
    void testDeleteSuccess(){
        long authorId = 1L;
        when(authorRepository.deleteById(authorId)).thenReturn(true);

        boolean result = authorService.delete(authorId);

        assertTrue(result);
    }

    @Test
    void testDeleteNotFound(){
        long authorId = 1L;
        when(authorRepository.deleteById(authorId)).thenReturn(false);

        boolean result = authorService.delete(authorId);

        assertFalse(result);
    }




















}
