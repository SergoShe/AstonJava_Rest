package com.github.SergoShe.repository.impl;

import com.github.SergoShe.model.Book;
import com.github.SergoShe.model.Reader;
import com.github.SergoShe.repository.ReaderRepository;
import com.github.SergoShe.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReaderRepositoryImpl implements ReaderRepository {
    private static final String INSERT_READER_SQL = "INSERT INTO readers (first_name, last_name) VALUES (?,?)";
    private static final String SELECT_READER_BY_ID = "SELECT * FROM readers WHERE id = ?";
    private static final String SELECT_ALL_READER = "SELECT * FROM readers";
    private static final String DELETE_READER_SQL = "DELETE FROM readers WHERE id = ?";
    private static final String UPDATE_READER_SQL = "UPDATE readers SET first_name = ?, last_name = ? WHERE id = ?";
    private static final String SELECT_BOOKS_BY_READER_ID = "SELECT * FROM books WHERE reader_id = ?";
    private static ReaderRepository instance;

    private ReaderRepositoryImpl() {
    }

    public static ReaderRepository getInstance() {
        if (instance == null) {
            instance = new ReaderRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void save(Reader reader) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_READER_SQL)) {
            statement.setString(1, reader.getFirstName());
            statement.setString(2, reader.getLastName());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                reader.setReaderId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean update(Reader reader) {
        boolean rowUpdate = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_READER_SQL)) {
            statement.setString(1, reader.getFirstName());
            statement.setString(2, reader.getLastName());
            statement.setLong(3, reader.getReaderId());
            rowUpdate = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdate;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean isDeleted = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_READER_SQL)) {
            statement.setLong(1, id);
            isDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    @Override
    public Optional<Reader> findById(Long id) {
        Reader reader = null;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_READER_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    reader = new Reader();
                    reader.setReaderId(resultSet.getLong(1));
                    reader.setFirstName(resultSet.getString(2));
                    reader.setLastName(resultSet.getString(3));
                    reader.setBooks(selectBooksByReaderId(reader.getReaderId(),connection));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(reader);
    }

    @Override
    public List<Reader> findAll() {
        List<Reader> readers = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_READER)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Reader reader = new Reader();
                reader.setReaderId(resultSet.getLong(1));
                reader.setFirstName(resultSet.getString(2));
                reader.setLastName(resultSet.getString(3));
                reader.setBooks(selectBooksByReaderId(reader.getReaderId(),connection));
                readers.add(reader);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readers;
    }

    private List<Book> selectBooksByReaderId(Long readerId, Connection connection){
        List<Book> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BOOKS_BY_READER_ID)){
            statement.setLong(1,readerId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Book book = new Book();
                book.setBookId(resultSet.getLong(1));
                book.setTitle(resultSet.getString(2));
                book.setYear(resultSet.getInt(3));
                books.add(book);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return books;
    }

}
