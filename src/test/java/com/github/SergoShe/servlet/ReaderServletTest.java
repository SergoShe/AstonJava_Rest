package com.github.SergoShe.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.SergoShe.DTO.ReaderDTO;
import com.github.SergoShe.service.ReaderService;
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
class ReaderServletTest {

    @InjectMocks
    private static ReaderServlet readerServlet;

    @Mock
    private ReaderService readerService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        readerServlet = new ReaderServlet(readerService);
    }

    @Test
    void testDoGetAll() throws ServletException, IOException {
        List<ReaderDTO> readerList = Collections.singletonList(new ReaderDTO(1L, "Sam", "Smith",null));
        when(readerService.getAll()).thenReturn(readerList);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(printWriter);

        readerServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).getWriter();

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(readerList), result);
    }

    @Test
    void testDoGet() throws ServletException, IOException {
        ReaderDTO readerDTO = new ReaderDTO(1L, "Sam", "Smith",null);
        when(readerService.getById(1L)).thenReturn(readerDTO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getPathInfo()).thenReturn("/1");
        when(response.getWriter()).thenReturn(printWriter);

        readerServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).getWriter();

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(readerDTO), result);
    }

    @Test
    void testDoGetByIdNotFound() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");
        when(readerService.getById(anyLong())).thenReturn(null);

        readerServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void testDoGetAuthorBadRequest() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/author");

        readerServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPost() throws ServletException, IOException {
        ReaderDTO readerDTO = new ReaderDTO(1L, "Sam", "Smith",null);
        ReaderDTO createdReaderDTO = new ReaderDTO(1L, "Sam", "Smith",null);
        when(readerService.create(any(ReaderDTO.class))).thenReturn(createdReaderDTO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        String json = mapper.writeValueAsString(readerDTO);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        readerServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).getWriter();

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(createdReaderDTO), result);
    }

    @Test
    void testDoPostBadRequest() throws ServletException, IOException {
        String invalidJson = "invalid json";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));

        readerServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPut() throws ServletException, IOException {
        ReaderDTO readerDTO = new ReaderDTO(1L, "Sam", "Smith",null);
        ReaderDTO updatedReaderDTO = new ReaderDTO(1L, "Sam", "Altman",null);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mapper.writeValueAsString(readerDTO))));
        when(readerService.update(any(ReaderDTO.class))).thenReturn(true);
        when(readerService.getById(readerDTO.getReaderId())).thenReturn(updatedReaderDTO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);

        readerServlet.doPut(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String result = stringWriter.toString();
        assertEquals(mapper.writeValueAsString(updatedReaderDTO), result);
    }

    @Test
    void testDoPutNotFound() throws ServletException, IOException {
        ReaderDTO readerDTO = new ReaderDTO(1L, "Sam", "Smith",null);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mapper.writeValueAsString(readerDTO))));
        when(readerService.update(any(ReaderDTO.class))).thenReturn(false);

        readerServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void testDoDelete() throws Exception {
        when(readerService.delete(1L)).thenReturn(true);
        when(request.getPathInfo()).thenReturn("/1");

        readerServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoDeleteNotFound() throws Exception {
        when(readerService.delete(1L)).thenReturn(false);
        when(request.getPathInfo()).thenReturn("/1");

        readerServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void testDoDeleteBadRequest() throws Exception {
        when(request.getPathInfo()).thenReturn("/");

        readerServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
