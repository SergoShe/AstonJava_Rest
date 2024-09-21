package com.github.SergoShe.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.SergoShe.DTO.AuthorDTO;
import com.github.SergoShe.service.AuthorService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServletTest {

    @InjectMocks
    private static AuthorServlet authorServlet;

    @Mock
    private AuthorService authorService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        authorServlet = new AuthorServlet(authorService);
    }

    @Test
    void testDoGetAll() throws ServletException, IOException {
        List<AuthorDTO> authorList = Collections.singletonList(new AuthorDTO(1L, "John", "Doe", null));
        when(authorService.getAll()).thenReturn(authorList);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(printWriter);

        authorServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).getWriter();

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(authorList), result);
    }

    @Test
    void testDoGet() throws ServletException, IOException {
        AuthorDTO authorDTO = new AuthorDTO(1L, "John", "Doe", null);
        when(authorService.getById(1L)).thenReturn(authorDTO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getPathInfo()).thenReturn("/1");
        when(response.getWriter()).thenReturn(printWriter);

        authorServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).getWriter();

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(authorDTO), result);
    }

    @Test
    void testDoGetByIdNotFound() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");
        when(authorService.getById(anyLong())).thenReturn(null);

        authorServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void testDoGetAuthorBadRequest() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/author");

        authorServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPost() throws ServletException, IOException {
        AuthorDTO authorDTO = new AuthorDTO(null, "John", "Doe", null);
        AuthorDTO createdAuthorDTO = new AuthorDTO(1L, "John", "Doe", null);
        when(authorService.create(any(AuthorDTO.class))).thenReturn(createdAuthorDTO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        String json = mapper.writeValueAsString(authorDTO);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        authorServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).getWriter();

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(createdAuthorDTO), result);
    }

    @Test
    void testDoPostBadRequest() throws ServletException, IOException {
        String invalidJson = "invalid json";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));

        authorServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPut() throws ServletException, IOException {
        AuthorDTO authorDTO = new AuthorDTO(1L, "John", "Doe", null);
        AuthorDTO updatedAuthorDTO = new AuthorDTO(1L, "John", "Doe", null);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mapper.writeValueAsString(authorDTO))));
        when(authorService.update(any(AuthorDTO.class))).thenReturn(true);
        when(authorService.getById(authorDTO.getAuthorId())).thenReturn(updatedAuthorDTO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);

        authorServlet.doPut(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(updatedAuthorDTO), result);
    }

    @Test
    void testDoPutNotFound() throws ServletException, IOException {
        AuthorDTO authorDTO = new AuthorDTO(null, "John", "Doe", null);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mapper.writeValueAsString(authorDTO))));
        when(authorService.update(any(AuthorDTO.class))).thenReturn(false);

        authorServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }


    @Test
    void testDoDelete() throws Exception {
        when(authorService.delete(1L)).thenReturn(true);
        when(request.getPathInfo()).thenReturn("/1");

        authorServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoDeleteNotFound() throws Exception {
        when(authorService.delete(1L)).thenReturn(false);
        when(request.getPathInfo()).thenReturn("/1");

        authorServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void testDoDeleteBadRequest() throws Exception {
        when(request.getPathInfo()).thenReturn("/");

        authorServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
