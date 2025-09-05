package com.example.bookstore.controller;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.book.BookRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static com.example.bookstore.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "classpath:db/delete-all-from-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.liquibase.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Create a new book with valid data")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_WithValidDto_ReturnsBookDto() throws Exception {
        CreateBookRequestDto requestDto = createDefaultBookRequestDto();

        MvcResult mvcResult = mockMvc.perform(post(BOOK_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "categoryIds")
                .isEqualTo(requestDto);
    }

    @Test
    @DisplayName("Create a new book with invalid data should return BadRequest status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_WithInvalidDto_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto invalidRequest = createInvalidRequestBookDto();

        mockMvc.perform(post(BOOK_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get all books with pagination")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:db/book/add-two-books.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllBooks_ReturnsValidPage() throws Exception {
        List<BookDto> expected = createListOfTwoBookDtos();

        MvcResult result = mockMvc.perform(get(BOOK_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String content = objectMapper.readTree(result.getResponse().getContentAsString()).get("content").toString();
        List<BookDto> actualBooks = objectMapper.readValue(content, new TypeReference<>() {});
        assertThat(actualBooks)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                        .withComparatorForType(BigDecimal::compareTo, BigDecimal.class).build())
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Get a book by valid id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:db/book/add-default-book.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getBookById_WithValidId_ReturnsBookDto() throws Exception {
        BookDto expected = createDefaultBookDto();

        MvcResult result = mockMvc.perform(get(BOOK_ID_URL, expected.getId()))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                        .withComparatorForType(BigDecimal::compareTo, BigDecimal.class).build())
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Get a book by invalid id should return NotFound status")
    @WithMockUser(username = "user", roles = "USER")
    void getBookById_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(BOOK_ID_URL, INVALID_BOOK_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update a book with valid data")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(
            scripts = {
                    "classpath:db/delete-all-from-tables.sql",
                    "classpath:db/book/add-default-book.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateBook_WithValidId_ReturnsBookDto() throws Exception {
        CreateBookRequestDto requestDto = createBookUpdateRequestDto();

        mockMvc.perform(put(BOOK_ID_URL, BOOK_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        Book updatedBookFromDb = bookRepository.findById(BOOK_ID).get();
        assertThat(updatedBookFromDb)
                .usingRecursiveComparison()
                .ignoringFields("id", "isDeleted", "categories")
                .isEqualTo(requestDto);
    }

    @Test
    @DisplayName("Update a book with invalid id should return NotFound status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_WithInvalidId_ReturnsNotFound() throws Exception {
        CreateBookRequestDto requestDto = createBookUpdateRequestDto();
        mockMvc.perform(put(BOOK_ID_URL, INVALID_BOOK_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete a book with valid id should return NoContent status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:db/book/add-default-book.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteBook_WithValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete(BOOK_ID_URL, BOOK_ID).with(csrf()))
                .andExpect(status().isNoContent());
        assertThat(bookRepository.findById(BOOK_ID)).isEmpty();
    }

    @Test
    @DisplayName("Delete a book with invalid id should return NotFound status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete(BOOK_ID_URL, INVALID_BOOK_ID).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Search for books by author")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:db/book/add-books-for-search-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchBooks_ReturnsValidPage() throws Exception {
        List<BookDto> expected = createBooksForAuthorSearch();
        MvcResult result = mockMvc.perform(get(BOOK_SEARCH_URL)
                        .param("author", AUTHOR_TO_SEARCH))
                .andExpect(status().isOk())
                .andReturn();

        String content = objectMapper.readTree(result.getResponse().getContentAsString()).get("content").toString();
        List<BookDto> actual = objectMapper.readValue(content, new TypeReference<>() {});

        assertThat(actual)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                        .withComparatorForType(BigDecimal::compareTo, BigDecimal.class).build())
                .isEqualTo(expected);
    }
}