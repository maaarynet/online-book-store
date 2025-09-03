package com.example.bookstore.controller;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.category.CategoryResponseDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.service.CategoryService;
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
public class CategoryControllerTest {
    private static final String CATEGORY_URL = "/categories";
    private static final String CATEGORY_ID_URL = CATEGORY_URL + "/{categoryId}";
    private static final String BOOKS_BY_CATEGORY_ID_URL = CATEGORY_ID_URL + "/books";
    private static final Long INVALID_CATEGORY_ID = 1000L;
    private static final Long CATEGORY_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create a new category with valid data")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_WithValidDto_ReturnsCategoryResponseDto() throws Exception {
        CategoryResponseDto categoryResponseDto = createDefaultCategoryResponseDto();
        when(categoryService.createCategory(any(CreateCategoryRequestDto.class))).thenReturn(categoryResponseDto);
        mockMvc.perform(post(CATEGORY_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDefaultCategoryRequestDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(categoryResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(categoryResponseDto.getName())));
    }

    @Test
    @DisplayName("Create a new category with invalid data should return BadRequest status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_WithInvalidDto_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(CATEGORY_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvalidRequestCategoryDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get all categories with pagination")
    @WithMockUser(username = "user", roles = "USER")
    void getAllCategories_ReturnsValidPage() throws Exception {
        CategoryResponseDto categoryResponseDto = createDefaultCategoryResponseDto();
        List<CategoryResponseDto> categories = List.of(categoryResponseDto);
        Page<CategoryResponseDto> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 10), categories.size());

        when(categoryService.getAll(any(Pageable.class))).thenReturn(categoryPage);
        mockMvc.perform(get("/categories?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(categories.size())))
                .andExpect(jsonPath("$.content[0].id", is(categoryResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(categoryResponseDto.getName())));
    }

    @Test
    @DisplayName("Get a category by valid id")
    @WithMockUser(username = "user", roles = "USER")
    void getCategoryById_WithValidId_ReturnsCategoryResponseDto() throws Exception {
        CategoryResponseDto categoryResponseDto = createDefaultCategoryResponseDto();

        when(categoryService.getCategoryById(any(Long.class))).thenReturn(categoryResponseDto);
        mockMvc.perform(get(CATEGORY_ID_URL, categoryResponseDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(categoryResponseDto.getName())));
    }

    @Test
    @DisplayName("Get a category by invalid id should return NotFound status")
    @WithMockUser(username = "user", roles = "USER")
    void getCategoryById_WithInvalidId_ReturnsNotFound() throws Exception {
        when(categoryService.getCategoryById(INVALID_CATEGORY_ID))
                .thenThrow(new EntityNotFoundException("Category not found with id: " + INVALID_CATEGORY_ID));
        mockMvc.perform(get(CATEGORY_ID_URL, INVALID_CATEGORY_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all books by valid category id")
    @WithMockUser(username = "user", roles = "USER")
    void getAllBooksByCategory_WithValidId_ReturnsValidData() throws Exception {
        BookDtoWithoutCategoryIds bookDto = createDefaultBookWithoutCategoryIdsDto();
        List<BookDtoWithoutCategoryIds> books = List.of(bookDto);
        Page<BookDtoWithoutCategoryIds> booksByCategoryPage = new PageImpl<>(books, PageRequest.of(0, 10), books.size());

        when(categoryService.getBooksByCategoryId(eq(CATEGORY_ID), any(Pageable.class))).thenReturn(booksByCategoryPage);
        mockMvc.perform(get(BOOKS_BY_CATEGORY_ID_URL, CATEGORY_ID)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(books.size())))
                .andExpect(jsonPath("$.content[0].id", is(bookDto.getId().intValue())))
                .andExpect(jsonPath("$.content[0].title", is(bookDto.getTitle())));
    }

    @Test
    @DisplayName("Get all books by invalid category id should return NotFound status")
    @WithMockUser(username = "user", roles = "USER")
    void getAllBooksByCategory_WithInvalidId_ReturnsNotFound() throws Exception {
        when(categoryService.getBooksByCategoryId(eq(INVALID_CATEGORY_ID), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("Category not found with id: " + INVALID_CATEGORY_ID));
        mockMvc.perform(get(BOOKS_BY_CATEGORY_ID_URL, INVALID_CATEGORY_ID)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update a category with valid data")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_WithValidId_ReturnsCategoryResponseDto() throws Exception {
        CategoryResponseDto expectedResponseDto = createUpdatedCategoryDto(CATEGORY_ID);

        when(categoryService.updateCategory(eq(CATEGORY_ID), any(CreateCategoryRequestDto.class))).thenReturn(expectedResponseDto);
        mockMvc.perform(put(CATEGORY_ID_URL, CATEGORY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCategoryUpdateRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedResponseDto.getName())));
    }

    @Test
    @DisplayName("Update a category with invalid id should return NotFound status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_WithInvalidId_ReturnsNotFound() throws Exception {
        when(categoryService.updateCategory(eq(INVALID_CATEGORY_ID), any(CreateCategoryRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Category not found with id: " + INVALID_CATEGORY_ID));
        mockMvc.perform(put(CATEGORY_ID_URL, INVALID_CATEGORY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDefaultCategoryRequestDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete a category with valid id should return NoContent status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCategory_WithValidId_ReturnsNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(CATEGORY_ID);
        mockMvc.perform(delete(CATEGORY_ID_URL, CATEGORY_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete a category with invalid id should return NotFound status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCategory_WithInvalidId_ReturnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Category not found with id: " + INVALID_CATEGORY_ID))
                .when(categoryService).deleteCategory(INVALID_CATEGORY_ID);
        mockMvc.perform(delete(CATEGORY_ID_URL, INVALID_CATEGORY_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
