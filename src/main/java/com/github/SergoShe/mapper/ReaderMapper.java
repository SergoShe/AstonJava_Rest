package com.github.SergoShe.mapper;

import com.github.SergoShe.DTO.ReaderDTO;
import com.github.SergoShe.model.Reader;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReaderMapper {
    ReaderMapper INSTANCE = Mappers.getMapper(ReaderMapper.class);


    ReaderDTO readerToReaderDTO(Reader reader);


    Reader readerDTOToReader(ReaderDTO readerDTO);

}