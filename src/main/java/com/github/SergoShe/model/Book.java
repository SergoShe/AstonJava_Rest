package com.github.SergoShe.model;

import java.util.List;

public class Book {

    private Long bookId;
    private String title;
    private int year;
    private List<Author> authors;
    private Reader reader;

    public Book() {
    }

    public Book(Long bookId, String title, int year, List<Author> authors, Reader reader) {
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

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }
}
