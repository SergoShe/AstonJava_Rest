package com.github.SergoShe.model;

import java.util.List;

public class Reader {
    private Long readerId;
    private String firstName;
    private String lastName;
    private List<Book> books;

    public Reader() {
    }

    public Reader(Long readerId, String firstName, String lastName, List<Book> books) {
        this.readerId = readerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.books = books;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
