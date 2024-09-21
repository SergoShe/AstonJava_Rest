package com.github.SergoShe.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.SergoShe.DTO.ReaderDTO;
import com.github.SergoShe.service.ReaderService;
import com.github.SergoShe.service.impl.ReaderServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/readers/*")
public class ReaderServlet extends HttpServlet {
    private final transient ReaderService readerService;
    private final ObjectMapper objectMapper;

    public ReaderServlet() {
        this.readerService = ReaderServiceImpl.getInstance();
        this.objectMapper = new ObjectMapper();
    }

    public ReaderServlet(ReaderService readerService) {
        this.readerService = readerService;
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
                List<ReaderDTO> readers = readerService.getAll();
                resp.getWriter().write(objectMapper.writeValueAsString(readers));
            } else {
                long id = Long.parseLong(pathInfo.substring(1));
                ReaderDTO reader = readerService.getById(id);
                if (reader != null) {
                    resp.getWriter().write(objectMapper.writeValueAsString(reader));
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
            ReaderDTO readerDTO = objectMapper.readValue(req.getReader(), ReaderDTO.class);
            ReaderDTO createdReader = readerService.create(readerDTO);
            setJsonHeaders(resp);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(createdReader));
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ReaderDTO readerDTO = objectMapper.readValue(req.getReader(), ReaderDTO.class);
            boolean updated = readerService.update(readerDTO);
            if (updated) {
                ReaderDTO updatedReader = readerService.getById(readerDTO.getReaderId());
                setJsonHeaders(resp);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(updatedReader));
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
            boolean deleted = readerService.delete(id);
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        }
    }
}
