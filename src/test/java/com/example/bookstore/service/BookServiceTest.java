package com.example.bookstore.service;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookSearchParametersDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.book.BookSpecificationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static com.example.bookstore.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private static final Long VALID_BOOK_ID = 1L;
    private static final Long INVALID_BOOK_ID = 100L;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String AUTHOR_TO_SEARCH = "F. Scott Fitzgerald";

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    private Book book;
    private BookDto bookDto;
    private CreateBookRequestDto createBookRequestDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        book = createDefaultBook();
        bookDto = createDefaultBookDto();
        createBookRequestDto = createDefaultBookRequestDto();
        pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    }

    @Test
    @DisplayName("Create a book with valid data should save and return a DTO")
    void createBook_WithValidDto_ShouldReturnBookDto() {
        when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookServiceImpl.createBook(createBookRequestDto);

        assertNotNull(actual);
        assertEquals(bookDto, actual);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Get all books should return a page of DTOs")
    void getAll_WhenBooksExist_ShouldReturnPageOfDtos() {
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        Page<BookDto> actualPage = bookServiceImpl.getAll(pageable);

        assertNotNull(actualPage);
        assertFalse(actualPage.isEmpty());
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(bookDto, actualPage.getContent().get(0));
        verify(bookRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Get all books when none exist should return an empty page")
    void getAll_WhenNoBooksExist_ShouldReturnEmptyPage() {
        when(bookRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<BookDto> actualPage = bookServiceImpl.getAll(pageable);

        assertNotNull(actualPage);
        assertTrue(actualPage.isEmpty());
        assertEquals(0, actualPage.getTotalElements());
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    @DisplayName("Get a book by a valid ID should return the correct DTO")
    void getBookById_WithValidId_ShouldReturnBookDto() {
        when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookServiceImpl.getBookById(VALID_BOOK_ID);

        assertEquals(bookDto, actual);
        verify(bookRepository).findById(VALID_BOOK_ID);
    }

    @Test
    @DisplayName("Get a book by an invalid ID should throw EntityNotFoundException")
    void getBookById_WithInvalidId_ShouldThrowException() {
        when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookServiceImpl.getBookById(INVALID_BOOK_ID)
        );
        assertEquals("Book not found with id: " + INVALID_BOOK_ID, exception.getMessage());
        verify(bookMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Update a book with a valid ID should return the updated DTO")
    void updateBook_WithValidId_ShouldReturnUpdatedDto() {
        CreateBookRequestDto updateRequestDto = createBookUpdateRequestDto();
        BookDto expectedResponseDto = createUpdatedBookDto(VALID_BOOK_ID);
        Book bookFromDb = createDefaultBook();

        when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(bookFromDb));
        when(bookRepository.save(bookFromDb)).thenReturn(bookFromDb);
        when(bookMapper.toDto(bookFromDb)).thenReturn(expectedResponseDto);

        BookDto actual = bookServiceImpl.updateBook(VALID_BOOK_ID, updateRequestDto);

        assertNotNull(actual);
        assertEquals(expectedResponseDto, actual);
        verify(bookMapper).updateBookFromDto(updateRequestDto, bookFromDb);
        verify(bookRepository).save(bookFromDb);
    }

    @Test
    @DisplayName("Update a book with an invalid ID should throw EntityNotFoundException")
    void updateBook_WithInvalidId_ShouldThrowException() {
        when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookServiceImpl.updateBook(INVALID_BOOK_ID, createBookRequestDto)
        );

        assertEquals("Book not found with id: " + INVALID_BOOK_ID, exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete a book by a valid ID should call the delete method")
    void deleteBook_WithValidId_ShouldCallDelete() {
        when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));

        bookServiceImpl.deleteBook(VALID_BOOK_ID);

        verify(bookRepository).delete(book);
    }

    @Test
    @DisplayName("Delete a book by an invalid ID should throw EntityNotFoundException")
    void deleteBook_WithInvalidId_ShouldThrowException() {
        when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookServiceImpl.deleteBook(INVALID_BOOK_ID)
        );

        assertEquals("Book not found with id: " + INVALID_BOOK_ID, exception.getMessage());
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    @DisplayName("Search for books should return a page of DTOs")
    void search_WithValidParams_ShouldReturnPageOfDtos() {
        BookSearchParametersDto searchParams = createBookSearchDtoByAuthor(AUTHOR_TO_SEARCH);
        Specification<Book> specification = mock(Specification.class);
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookSpecificationBuilder.build(searchParams)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> actualPage = bookServiceImpl.search(searchParams, pageable);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(bookDto, actualPage.getContent().get(0));
    }

    @Test
    @DisplayName("Search for books with criteria that match nothing should return an empty page")
    void search_WhenNoBooksMatch_ShouldReturnEmptyPage() {
        BookSearchParametersDto searchParams = createBookSearchDtoByAuthor("NonExistent Author");
        Specification<Book> specification = mock(Specification.class);

        when(bookSpecificationBuilder.build(searchParams)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(Page.empty(pageable));
        Page<BookDto> actualPage = bookServiceImpl.search(searchParams, pageable);

        assertNotNull(actualPage);
        assertTrue(actualPage.isEmpty());
        assertEquals(0, actualPage.getTotalElements());
        verify(bookMapper, never()).toDto(any(Book.class));
    }
}