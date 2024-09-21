package com.github.SergoShe.repository.impl;

import com.github.SergoShe.model.Author;
import com.github.SergoShe.model.Book;
import com.github.SergoShe.repository.AuthorRepository;
import com.github.SergoShe.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthorRepositoryImpl implements AuthorRepository {
    private static final String INSERT_AUTHOR_SQL = "INSERT INTO authors (first_name, last_name) VALUES (?, ?)";
    private static final String DELETE_AUTHOR_SQL = "DELETE FROM authors WHERE id = ?";
    private static final String SELECT_AUTHOR_BY_ID = "SELECT * FROM authors WHERE id = ?";
    private static final String SELECT_ALL_AUTHOR = "SELECT * FROM authors";
    private static final String UPDATE_AUTHOR_SQL = "UPDATE authors SET first_name = ?, last_name = ? WHERE id = ?";
    private static final String SELECT_BOOKS_BY_AUTHOR_ID = "SELECT b.id, b.title, b.year, b.reader_id FROM books b " +
            "JOIN book_authors ba ON b.id = ba.book_id WHERE ba.author_id = ?";
    private static final String INSERT_BOOK_AUTHOR_SQL = "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?)";
    private static final String DELETE_BOOK_AUTHOR_SQL = "DELETE FROM book_authors WHERE author_id = ?";

    private static AuthorRepository instance;

    private AuthorRepositoryImpl() {
    }

    public static synchronized AuthorRepository getInstance() {
        if (instance == null) {
            instance = new AuthorRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Author save(Author author) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_AUTHOR_SQL)) {
            statement.setString(1, author.getFirstName());
            statement.setString(2, author.getLastName());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                author.setAuthorId(generatedKeys.getLong(1));
            }
            if (author.getBooks() != null) {
                insertBooksAuthors(author, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return author;
    }

    @Override
    public boolean update(Author author) {
        boolean rowUpdated = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_AUTHOR_SQL)) {
            statement.setString(1, author.getFirstName());
            statement.setString(2, author.getLastName());
            statement.setLong(3, author.getAuthorId());
            updateBooksAuthors(author, connection);
            rowUpdated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean isDeleted = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_AUTHOR_SQL)) {
            statement.setLong(1, id);
            isDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    @Override
    public Optional<Author> findById(Long id) {
        Author author = null;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_AUTHOR_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    author = new Author();
                    author.setAuthorId(resultSet.getLong(1));
                    author.setFirstName(resultSet.getString(2));
                    author.setLastName(resultSet.getString(3));
                    author.setBooks(findBooksByAuthorId(id, connection));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(author);
    }

    @Override
    public List<Author> findAll() {
        List<Author> authors = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_AUTHOR)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Author author = new Author();
                author.setAuthorId(resultSet.getLong(1));
                author.setFirstName(resultSet.getString(2));
                author.setLastName(resultSet.getString(3));
                author.setBooks(findBooksByAuthorId(author.getAuthorId(), connection));
                authors.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    @Override
    public boolean existsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String sqlQuery = "SELECT COUNT(*) FROM authors WHERE id IN (" +
                ids.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1) == ids.size();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<Book> findBooksByAuthorId(Long authorId, Connection connection) {
        List<Book> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BOOKS_BY_AUTHOR_ID)) {
            statement.setLong(1, authorId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.setBookId(resultSet.getLong(1));
                book.setTitle(resultSet.getString(2));
                book.setYear(resultSet.getInt(3));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private void insertBooksAuthors(Author author, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BOOK_AUTHOR_SQL)) {
            for (Book book : author.getBooks()) {
                statement.setLong(1, book.getBookId());
                statement.setLong(2, author.getAuthorId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBooksAuthors(Author author, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BOOK_AUTHOR_SQL)) {
            statement.setLong(1, author.getAuthorId());
            statement.executeUpdate();
            insertBooksAuthors(author, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
