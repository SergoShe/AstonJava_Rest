package com.github.SergoShe.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.SergoShe.DTO.AuthorDTO;
import com.github.SergoShe.service.AuthorService;
import com.github.SergoShe.service.impl.AuthorServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/authors/*")
public class AuthorServlet extends HttpServlet {
    private final transient AuthorService authorService;
    private final ObjectMapper objectMapper;

    public AuthorServlet() {
        this.authorService = AuthorServiceImpl.getInstance();
        this.objectMapper = new ObjectMapper();
    }

    public AuthorServlet(AuthorService authorService) {
        this.authorService = authorService;
        this.objectMapper = new ObjectMapper();
    }

    private static void setJsonHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        setJsonHeaders(resp);
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<AuthorDTO> authors = authorService.getAll();
                resp.getWriter().write(objectMapper.writeValueAsString(authors));
            } else {
                long id = Long.parseLong(pathInfo.substring(1));
                AuthorDTO author = authorService.getById(id);
                if (author != null) {
                    resp.getWriter().write(objectMapper.writeValueAsString(author));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            AuthorDTO authorDTO = objectMapper.readValue(req.getReader(), AuthorDTO.class);
            AuthorDTO createdAuthor = authorService.create(authorDTO);
            setJsonHeaders(resp);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(createdAuthor));
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            AuthorDTO authorDTO = objectMapper.readValue(req.getReader(), AuthorDTO.class);
            boolean updated = authorService.update(authorDTO);
            if (updated) {
                AuthorDTO updatedAuthor = authorService.getById(authorDTO.getAuthorId());
                setJsonHeaders(resp);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(updatedAuthor));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long id = Long.parseLong(req.getPathInfo().substring(1));
            boolean deleted = authorService.delete(id);
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
