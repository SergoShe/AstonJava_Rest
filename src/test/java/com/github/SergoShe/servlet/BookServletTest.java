package com.github.SergoShe.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.SergoShe.DTO.BookDTO;
import com.github.SergoShe.service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServletTest {

    @InjectMocks
    private BookServlet bookServlet;

    @Mock
    private BookService bookService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        bookServlet = new BookServlet(bookService);
    }

    @Test
    void testDoGetAll() throws ServletException, IOException {
        List<BookDTO> bookList = Collections.singletonList(new BookDTO(1L, "1984", 1949, null, null));
        when(bookService.getAll()).thenReturn(bookList);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(printWriter);

        bookServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).getWriter();

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(bookList), result);
    }

    @Test
    void testDoGet() throws ServletException, IOException {
        BookDTO bookDTO = new BookDTO(1L, "1984", 1949, null, null);
        when(bookService.getById(1L)).thenReturn(bookDTO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getPathInfo()).thenReturn("/1");
        when(response.getWriter()).thenReturn(printWriter);

        bookServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).getWriter();

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(bookDTO), result);
    }

    @Test
    void testDoGetByIdNotFound() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");
        when(bookService.getById(anyLong())).thenReturn(null);

        bookServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void testDoGetBookBadRequest() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/author");

        bookServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPost() throws ServletException, IOException {
        BookDTO bookDTO = new BookDTO(null, "1984", 1949, null, null);
        BookDTO createdBookDTO = new BookDTO(1L, "1984", 1949, null, null);
        when(bookService.create(any(BookDTO.class))).thenReturn(createdBookDTO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        String json = mapper.writeValueAsString(bookDTO);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        bookServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).getWriter();

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(createdBookDTO), result);
    }

    @Test
    void testDoPostBadRequest() throws ServletException, IOException {
        String invalidJson = "invalid json";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));

        bookServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPut() throws ServletException, IOException {
        BookDTO bookDTO = new BookDTO(1L, "1984", 1949, null, null);
        BookDTO updatedBookDTO = new BookDTO(1L, "1984 part 2", 1959, null, null);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mapper.writeValueAsString(bookDTO))));
        when(bookService.update(any(BookDTO.class))).thenReturn(true);
        when(bookService.getById(bookDTO.getBookId())).thenReturn(updatedBookDTO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);

        bookServlet.doPut(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(updatedBookDTO), result);
    }

    @Test
    void testDoPutNotFound() throws ServletException, IOException {
        BookDTO bookDTO = new BookDTO(1L, "1984", 1949, null, null);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mapper.writeValueAsString(bookDTO))));
        when(bookService.update(any(BookDTO.class))).thenReturn(false);

        bookServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void testDoDelete() throws Exception {
        when(bookService.delete(1L)).thenReturn(true);
        when(request.getPathInfo()).thenReturn("/1");

        bookServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoDeleteNotFound() throws Exception {
        when(bookService.delete(1L)).thenReturn(false);
        when(request.getPathInfo()).thenReturn("/1");

        bookServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void testDoDeleteBadRequest() throws Exception {
        when(request.getPathInfo()).thenReturn("/");

        bookServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
