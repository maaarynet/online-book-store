package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookRequestDto bookDto);

    List<BookDto> getAll();

    BookDto getBookById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto bookDto);

    void deleteBook(Long id);
}
