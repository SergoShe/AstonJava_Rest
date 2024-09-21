package com.github.SergoShe.service;

import com.github.SergoShe.DTO.BookDTO;
import com.github.SergoShe.DTO.ReaderDTO;
import com.github.SergoShe.model.Reader;
import com.github.SergoShe.repository.ReaderRepository;
import com.github.SergoShe.service.impl.ReaderServiceImpl;
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
public class ReaderServiceTest {

    @InjectMocks
    private ReaderServiceImpl readerService;

    @Mock
    private ReaderRepository readerRepository;

    @Captor
    private ArgumentCaptor<Reader> readerCaptor;

    @BeforeEach
    public void setUp() {
        readerService = new ReaderServiceImpl(readerRepository);
    }

    @Test
    void testCreateSuccess(){
        BookDTO bookDTO = new BookDTO(1L, "Title", 2020, null, null);
        ReaderDTO readerDTO = new ReaderDTO(1L, "firstName", "surName", Collections.singletonList(bookDTO));

        when(readerRepository.save(any(Reader.class))).thenAnswer(invocation ->{
            Reader reader = invocation.getArgument(0);
            reader.setReaderId(1L);
            return reader;
        });

        ReaderDTO createdReaderDTO = readerService.create(readerDTO);

        verify(readerRepository).save(readerCaptor.capture());
        Reader capturedReader = readerCaptor.getValue();

        assertNotNull(createdReaderDTO.getReaderId());
        assertEquals(readerDTO.getFirstName(), capturedReader.getFirstName());
        assertEquals(readerDTO.getLastName(), capturedReader.getLastName());
    }

    @Test
    void testGetByIdSuccess(){
        long readerId = 1L;
        Reader reader = new Reader(readerId, "firstName", "surName", Collections.emptyList());
        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));

        ReaderDTO readerDTO = readerService.getById(readerId);

        assertEquals(readerId, readerDTO.getReaderId());
        assertEquals(reader.getFirstName(), readerDTO.getFirstName());
    }

    @Test
    void testGetByIdNotFound() {
        long readerId = 1L;
        when(readerRepository.findById(readerId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> readerService.getById(readerId));
        assertEquals("Reader not found", exception.getMessage());
    }

    @Test
    void testGetAll() {
        Reader reader = new Reader(1L, "firstName", "surName", Collections.emptyList());
        when(readerRepository.findAll()).thenReturn(Collections.singletonList(reader));

        List<ReaderDTO> readerDTOS = readerService.getAll();

        assertEquals(1, readerDTOS.size());
        assertEquals(reader.getFirstName(), readerDTOS.get(0).getFirstName());
    }

    @Test
    void testUpdateSuccess() {
        ReaderDTO readerDTO = new ReaderDTO(1L, "firstName", "surName", null);

        when(readerRepository.update(any(Reader.class))).thenReturn(true);

        boolean result = readerService.update(readerDTO);

        assertTrue(result);
        verify(readerRepository).update(readerCaptor.capture());
        Reader capturedAuthor = readerCaptor.getValue();
        assertEquals(readerDTO.getFirstName(),capturedAuthor.getFirstName());
    }

    @Test
    void testUpdateNotFound(){
        ReaderDTO readerDTO = new ReaderDTO(1L, "firstName", "surName", null);

        when(readerRepository.update(any(Reader.class))).thenReturn(false);

        boolean result = readerService.update(readerDTO);

        assertFalse(result);
    }

    @Test
    void testDeleteSuccess(){
        long readerId = 1L;
        when(readerRepository.deleteById(readerId)).thenReturn(true);

        boolean result = readerService.delete(readerId);

        assertTrue(result);
    }

    @Test
    void testDeleteNotFound(){
        long readerId = 1L;
        when(readerRepository.deleteById(readerId)).thenReturn(false);

        boolean result = readerService.delete(readerId);

        assertFalse(result);
    }
}
