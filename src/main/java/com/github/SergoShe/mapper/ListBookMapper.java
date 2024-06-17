package com.github.SergoShe.mapper;

import com.github.SergoShe.DTO.BookDTO;
import com.github.SergoShe.model.Book;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ListBookMapper {

    List<BookDTO> toListBookDTO(List<Book> books);

    List<Book> toListBook(List<BookDTO> bookDTOs);
}
