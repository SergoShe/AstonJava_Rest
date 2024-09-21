package com.github.SergoShe.DTO;

import java.util.List;

public class ReaderDTO {
    private Long readerId;
    private String firstName;
    private String lastName;
    private List<BookDTO> books;

    public ReaderDTO() {
    }

    public ReaderDTO(Long readerId, String firstName, String lastName, List<BookDTO> books) {
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

    public List<BookDTO> getBooks() {
        return books;
    }

    public void setBooks(List<BookDTO> books) {
        this.books = books;
    }
}
