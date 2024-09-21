package com.github.SergoShe.DTO;

import java.util.List;

public class BookDTO {

    private Long bookId;
    private String title;
    private int year;
    private List<AuthorDTO> authors;
    private ReaderDTO reader;

    public BookDTO() {
    }

    public BookDTO(Long bookId, String title, int year, List<AuthorDTO> authors, ReaderDTO reader) {
        this.bookId = bookId;
        this.title = title;
        this.year = year;
        this.authors = authors;
        this.reader = reader;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<AuthorDTO> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorDTO> authors) {
        this.authors = authors;
    }

    public ReaderDTO getReader() {
        return reader;
    }

    public void setReader(ReaderDTO reader) {
        this.reader = reader;
    }
}
