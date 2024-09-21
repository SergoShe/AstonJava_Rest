package com.github.SergoShe.mapper;

import com.github.SergoShe.DTO.AuthorDTO;
import com.github.SergoShe.model.Author;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ListAuthorMapper {

    List<AuthorDTO> toListAuthorDTO(List<Author> authors);

    List<Author> toListAuthor(List<AuthorDTO> authorDTO);
}
