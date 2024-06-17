package com.github.SergoShe.repository.impl;

import com.github.SergoShe.model.Author;
import com.github.SergoShe.model.Book;
import com.github.SergoShe.model.Reader;
import com.github.SergoShe.repository.BookRepository;
import com.github.SergoShe.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepositoryImpl implements BookRepository {
    private static final String INSERT_BOOK_SQL = "INSERT INTO books (title, year, reader_id) VALUES (?, ?, ?)";
    private static final String SELECT_BOOK_BY_ID = "SELECT * FROM books WHERE id = ?";
    private static final String SELECT_ALL_BOOKS = "SELECT * FROM books";
    private static final String DELETE_BOOK_SQL = "DELETE FROM books WHERE id = ?";
    private static final String UPDATE_BOOK_SQL = "UPDATE books SET title = ?, year = ?, reader_id = ? WHERE id = ?";
    private static final String SELECT_AUTHORS_BY_BOOK_ID = "SELECT a.* FROM authors a JOIN book_authors ba ON a.id = ba.author_id WHERE ba.book_id = ?";
    private static final String SELECT_READER_BY_BOOK_ID = "SELECT r.* FROM readers r JOIN books b ON r.id = b.reader_id WHERE b.id = ?";
    private static final String INSERT_BOOK_AUTHOR_SQL = "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?)";
    private static final String DELETE_BOOK_AUTHOR_SQL = "DELETE FROM book_authors WHERE book_id = ?";

    private static BookRepository instance;

    private BookRepositoryImpl() {
    }

    public static synchronized BookRepository getInstance() {
        if (instance == null) {
            instance = new BookRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void save(Book book) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_BOOK_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getTitle());
            statement.setInt(2, book.getYear());
            if (book.getReader() != null) {
                statement.setLong(3, book.getReader().getReaderId());
            } else {
                statement.setNull(3, Types.NULL);
            }
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                book.setBookId(generatedKeys.getLong(1));
            }
            insertBooksAuthors(book, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertBooksAuthors(Book book, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BOOK_AUTHOR_SQL)) {
            for (Author author : book.getAuthors()) {
                statement.setLong(1, book.getBookId());
                statement.setLong(2, author.getAuthorId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        Book book = null;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BOOK_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    book = new Book();
                    book.setBookId(resultSet.getLong(1));
                    book.setTitle(resultSet.getString(2));
                    book.setYear(resultSet.getInt(3));
                    book.setAuthors(findAuthorsByBookId(resultSet.getLong(1), connection));
                    book.setReader(findReaderByBookId(resultSet.getLong(1), connection));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(book);
    }

    private List<Author> findAuthorsByBookId(Long bookId, Connection connection) {
        List<Author> authors = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_AUTHORS_BY_BOOK_ID)) {
            statement.setLong(1, bookId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Author author = new Author();
                author.setAuthorId(resultSet.getLong(1));
                author.setFirstName(resultSet.getString(2));
                author.setLastName(resultSet.getString(3));
                authors.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    private Reader findReaderByBookId(Long bookId, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_READER_BY_BOOK_ID)) {
            statement.setLong(1, bookId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Reader reader = new Reader();
                reader.setReaderId(resultSet.getLong(1));
                reader.setFirstName(resultSet.getString(2));
                reader.setLastName(resultSet.getString(3));
                return reader;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_BOOKS)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.setBookId(resultSet.getLong(1));
                book.setTitle(resultSet.getString(2));
                book.setYear(resultSet.getInt(3));
                book.setAuthors(findAuthorsByBookId(resultSet.getLong(1), connection));
                book.setReader(findReaderByBookId(resultSet.getLong(1), connection));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public boolean deleteById(Long id) {
        boolean isDeleted = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BOOK_SQL)) {
            statement.setLong(1, id);
            isDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    public boolean update(Book book) {
        boolean rowUpdated = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOK_SQL)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setInt(2, book.getYear());
            preparedStatement.setLong(3, book.getReader() != null ? book.getReader().getReaderId() : null);
            preparedStatement.setLong(4, book.getBookId());
            updateBooksAuthors(book, connection);
            rowUpdated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    private void updateBooksAuthors(Book book, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BOOK_AUTHOR_SQL)) {
            statement.setLong(1, book.getBookId());
            statement.executeUpdate();
            insertBooksAuthors(book, connection);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


}

