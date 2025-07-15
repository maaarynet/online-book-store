package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.CreateBookRequestDto;
import com.example.bookstore.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    BookDto createBook(CreateBookRequestDto bookDto);

    List<Book> getAll();

    Optional<Book> getBookById(Long id);
}
