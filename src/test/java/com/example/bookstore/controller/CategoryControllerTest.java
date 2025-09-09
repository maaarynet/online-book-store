package com.example.bookstore.controller;

import static com.example.bookstore.util.TestUtil.createCategoryUpdateRequestDto;
import static com.example.bookstore.util.TestUtil.createDefaultBookWithoutCategoryIdsDto;
import static com.example.bookstore.util.TestUtil.createDefaultCategoryRequestDto;
import static com.example.bookstore.util.TestUtil.createDefaultCategoryResponseDto;
import static com.example.bookstore.util.TestUtil.createInvalidRequestCategoryDto;
import static com.example.bookstore.util.TestUtil.createListOfTwoCategoryDtos;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryResponseDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.category.CategoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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

@Sql(scripts = "classpath:db/delete-all-from-tables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Create a new category with valid data")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_WithValidDto_ReturnsCategoryResponseDto() throws Exception {
        CreateCategoryRequestDto requestDto = createDefaultCategoryRequestDto();

        MvcResult mvcResult = mockMvc.perform(post(CATEGORY_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), CategoryResponseDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(requestDto);
    }

    @Test
    @DisplayName("Create a new category with invalid data should return BadRequest status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_WithInvalidDto_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(CATEGORY_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(createInvalidRequestCategoryDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get all categories with pagination")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {
            "classpath:db/delete-all-from-tables.sql",
            "classpath:db/category/add-two-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllCategories_ReturnsValidPage() throws Exception {
        List<CategoryResponseDto> expected = createListOfTwoCategoryDtos();

        MvcResult result = mockMvc.perform(get(CATEGORY_URL).param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String content = objectMapper.readTree(
                result.getResponse().getContentAsString()
        ).get("content").toString();
        List<CategoryResponseDto> actual = objectMapper.readValue(content,
                new TypeReference<>() {});
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Get a category by valid id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {
            "classpath:db/delete-all-from-tables.sql",
            "classpath:db/category/add-default-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getCategoryById_WithValidId_ReturnsCategoryResponseDto() throws Exception {
        CategoryResponseDto expected = createDefaultCategoryResponseDto();

        MvcResult result = mockMvc.perform(get(CATEGORY_ID_URL, CATEGORY_ID))
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Get a category by invalid id should return NotFound status")
    @WithMockUser(username = "user", roles = "USER")
    void getCategoryById_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(CATEGORY_ID_URL, INVALID_CATEGORY_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all books by valid category id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {
            "classpath:db/delete-all-from-tables.sql",
            "classpath:db/category/add-book-with-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllBooksByCategory_WithValidId_ReturnsValidData() throws Exception {
        List<BookDtoWithoutCategoryIds> expected = List.of(
                createDefaultBookWithoutCategoryIdsDto());

        MvcResult result = mockMvc.perform(get(BOOKS_BY_CATEGORY_ID_URL, CATEGORY_ID)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String content = objectMapper.readTree(
                result.getResponse().getContentAsString()
        ).get("content").toString();
        List<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(content,
                new TypeReference<>() {});
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Get all books by invalid category id should return NotFound status")
    @WithMockUser(username = "user", roles = "USER")
    void getAllBooksByCategory_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(BOOKS_BY_CATEGORY_ID_URL, INVALID_CATEGORY_ID)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update a category with valid data")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:db/category/add-default-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateCategory_WithValidId_ReturnsCategoryResponseDto() throws Exception {
        CreateCategoryRequestDto requestDto = createCategoryUpdateRequestDto();

        mockMvc.perform(put(CATEGORY_ID_URL, CATEGORY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        Category updatedCategory = categoryRepository.findById(CATEGORY_ID).get();
        assertThat(updatedCategory.getName()).isEqualTo(requestDto.getName());
        assertThat(updatedCategory.getDescription()).isEqualTo(requestDto.getDescription());
    }

    @Test
    @DisplayName("Update a category with invalid id should return NotFound status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(put(CATEGORY_ID_URL, INVALID_CATEGORY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(createDefaultCategoryRequestDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete a category with valid id should return NoContent status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = {
            "classpath:db/delete-all-from-tables.sql",
            "classpath:db/category/add-default-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteCategory_WithValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete(CATEGORY_ID_URL, CATEGORY_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        assertThat(categoryRepository.findById(CATEGORY_ID)).isEmpty();
    }

    @Test
    @DisplayName("Delete a category with invalid id should return NotFound status")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCategory_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete(CATEGORY_ID_URL, INVALID_CATEGORY_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
