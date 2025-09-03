package com.example.bookstore.controller;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookSearchParametersDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static com.example.bookstore.util.TestUtil.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {
    private static final String BOOK_URL = "/books";
    private static final String BOOK_ID_URL = BOOK_URL + "/{id}";
    private static final String BOOK_SEARCH_URL = BOOK_URL + "/search";
    private static final Long INVALID_BOOK_ID = 1000L;
    private static final Long BOOK_ID = 1L;
    private static final String AUTHOR_TO_SEARCH = "F. Scott Fitzgerald";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create a new book with valid data")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_WithValidDto_ReturnsBookDto() throws Exception {
        BookDto bookDto = createDefaultBookDto();
        when(bookService.createBook(any(CreateBookRequestDto.class))).thenReturn(bookDto);
        mockMvc.perform(post(BOOK_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDefaultBookRequestDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookDto.getId().intValue())))
                .andExpect(jsonPath("$.title", is(bookDto.getTitle())));
    }

    @Test
    @DisplayName("Create a new book with invalid data should return BadRequest status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_WithInvalidDto_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(BOOK_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvalidRequestBookDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get all books with pagination")
    @WithMockUser(username = "user", roles = "USER")
    void getAllBooks_ReturnsValidPage() throws Exception {
        BookDto bookDto = createDefaultBookDto();
        List<BookDto> books = List.of(bookDto);
        Page<BookDto> bookPage = new PageImpl<>(books, PageRequest.of(0, 10), books.size());

        when(bookService.getAll(any(Pageable.class))).thenReturn(bookPage);
        mockMvc.perform(get(BOOK_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(books.size())))
                .andExpect(jsonPath("$.content[0].id", is(bookDto.getId().intValue())))
                .andExpect(jsonPath("$.content[0].title", is(bookDto.getTitle())));
    }

    @Test
    @DisplayName("Get a book by valid id")
    @WithMockUser(username = "user", roles = "USER")
    void getBookById_WithValidId_ReturnsBookDto() throws Exception {
        BookDto bookDto = createDefaultBookDto();

        when(bookService.getBookById(any(Long.class))).thenReturn(bookDto);
        mockMvc.perform(get(BOOK_ID_URL, bookDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookDto.getId().intValue())))
                .andExpect(jsonPath("$.title", is(bookDto.getTitle())));
    }

    @Test
    @DisplayName("Get a book by invalid id should return NotFound status")
    @WithMockUser(username = "user", roles = "USER")
    void getBookById_WithInvalidId_ReturnsNotFound() throws Exception {
        when(bookService.getBookById(INVALID_BOOK_ID))
                .thenThrow(new EntityNotFoundException("Book not found with id: " + INVALID_BOOK_ID));
        mockMvc.perform(get(BOOK_ID_URL, INVALID_BOOK_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update a book with valid data")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_WithValidId_ReturnsBookDto() throws Exception {
        BookDto expectedResponseDto = createUpdatedBookDto(BOOK_ID);

        when(bookService.updateBook(eq(BOOK_ID), any(CreateBookRequestDto.class))).thenReturn(expectedResponseDto);
        mockMvc.perform(put(BOOK_ID_URL, BOOK_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookUpdateRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.title", is(expectedResponseDto.getTitle())));
    }

    @Test
    @DisplayName("Update a book with invalid id should return NotFound status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_WithInvalidId_ReturnsNotFound() throws Exception {
        when(bookService.updateBook(eq(INVALID_BOOK_ID), any(CreateBookRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Book not found with id: " + INVALID_BOOK_ID));
        mockMvc.perform(put(BOOK_ID_URL, INVALID_BOOK_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDefaultBookRequestDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete a book with valid id should return NoContent status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_WithValidId_ReturnsNoContent() throws Exception {
        doNothing().when(bookService).deleteBook(BOOK_ID);
        mockMvc.perform(delete(BOOK_ID_URL, BOOK_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete a book with invalid id should return NotFound status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_WithInvalidId_ReturnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Book not found with id: " + INVALID_BOOK_ID))
                .when(bookService).deleteBook(INVALID_BOOK_ID);
        mockMvc.perform(delete(BOOK_ID_URL, INVALID_BOOK_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Search for books by parameters: authors, titles, isbns. With pagination")
    @WithMockUser(username = "user", roles = "USER")
    void searchBooks_ReturnsValidPage() throws Exception {
        BookSearchParametersDto searchParams = createBookSearchDtoByAuthor(AUTHOR_TO_SEARCH);
        BookDto bookDto = createDefaultBookDto();
        List<BookDto> books = List.of(bookDto);
        Page<BookDto> bookPage = new PageImpl<>(books, PageRequest.of(0, 10), books.size());

        when(bookService.search(
                argThat(params -> params.author() != null && params.author()[0].equals(AUTHOR_TO_SEARCH)),
                any(Pageable.class)))
                .thenReturn(bookPage);
        mockMvc.perform(get(BOOK_SEARCH_URL)
                        .param("author", AUTHOR_TO_SEARCH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(books.size())))
                .andExpect(jsonPath("$.content[0].id", is(bookDto.getId().intValue())))
                .andExpect(jsonPath("$.content[0].author", is(AUTHOR_TO_SEARCH)));
    }
}