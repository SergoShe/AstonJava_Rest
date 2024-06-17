package com.github.SergoShe.service.impl;

import com.github.SergoShe.DTO.ReaderDTO;
import com.github.SergoShe.mapper.ReaderMapper;
import com.github.SergoShe.model.Reader;
import com.github.SergoShe.repository.ReaderRepository;
import com.github.SergoShe.repository.impl.ReaderRepositoryImpl;
import com.github.SergoShe.service.ReaderService;

import java.util.List;
import java.util.stream.Collectors;

public class ReaderServiceImpl implements ReaderService {
    private final ReaderRepository readerRepository;
    private final ReaderMapper readerMapper;
    private static ReaderServiceImpl readerServiceImpl;

    public ReaderServiceImpl() {
        this.readerRepository = ReaderRepositoryImpl.getInstance();
        this.readerMapper = ReaderMapper.INSTANCE;
    }

    public static ReaderServiceImpl getInstance() {
        if (readerServiceImpl == null) {
            readerServiceImpl = new ReaderServiceImpl();
        }
        return readerServiceImpl;
    }

    @Override
    public ReaderDTO create(ReaderDTO readerDTO) {
        Reader reader = readerMapper.readerDTOToReader(readerDTO);
        readerRepository.save(reader);
        return readerMapper.readerToReaderDTO(reader);
    }

    @Override
    public ReaderDTO getById(Long id) {
        Reader reader = readerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Reader not found"));
        return readerMapper.readerToReaderDTO(reader);
    }

    @Override
    public List<ReaderDTO> getAll() {
        List<Reader> readers = readerRepository.findAll();
        return readers.stream().map(readerMapper::readerToReaderDTO).collect(Collectors.toList());
    }

    @Override
    public boolean update(ReaderDTO readerDTO) {
        Reader reader = readerMapper.readerDTOToReader(readerDTO);
        return readerRepository.update(reader);
    }

    @Override
    public boolean delete(Long id) {
        return readerRepository.deleteById(id);
    }
}