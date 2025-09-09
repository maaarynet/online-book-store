package com.example.bookstore.service;

import static com.example.bookstore.util.TestUtil.createCategoryUpdateRequestDto;
import static com.example.bookstore.util.TestUtil.createDefaultBook;
import static com.example.bookstore.util.TestUtil.createDefaultBookWithoutCategoryIdsDto;
import static com.example.bookstore.util.TestUtil.createDefaultCategory;
import static com.example.bookstore.util.TestUtil.createDefaultCategoryRequestDto;
import static com.example.bookstore.util.TestUtil.createDefaultCategoryResponseDto;
import static com.example.bookstore.util.TestUtil.createUpdatedCategoryDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryResponseDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.mapper.CategoryMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.category.CategoryRepository;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private static final Long VALID_CATEGORY_ID = 1L;
    private static final Long INVALID_CATEGORY_ID = 100L;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    private Category category;
    private CategoryResponseDto categoryResponseDto;
    private CreateCategoryRequestDto createCategoryRequestDto;
    private Book book;
    private BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        category = createDefaultCategory();
        categoryResponseDto = createDefaultCategoryResponseDto();
        createCategoryRequestDto = createDefaultCategoryRequestDto();
        book = createDefaultBook();
        bookDtoWithoutCategoryIds = createDefaultBookWithoutCategoryIdsDto();
        pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    }

    @Test
    @DisplayName("Create a category with valid data should save and return a DTO")
    void createCategory_WithValidDto_ShouldReturnCategoryDto() {
        when(categoryMapper.toModel(createCategoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto actual = categoryServiceImpl.createCategory(createCategoryRequestDto);

        assertNotNull(actual);
        assertEquals(categoryResponseDto, actual);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Get all categories should return a page of DTOs")
    void getAll_WhenCategoriesExist_ShouldReturnPageOfDtos() {
        Page<Category> categoryPage = new PageImpl<>(List.of(category), pageable, 1);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDto);

        Page<CategoryResponseDto> actualPage = categoryServiceImpl.getAll(pageable);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(categoryResponseDto, actualPage.getContent().get(0));
    }

    @Test
    @DisplayName("Get all categories when none exist should return an empty page")
    void getAll_WhenNoCategoriesExist_ShouldReturnEmptyPage() {
        when(categoryRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<CategoryResponseDto> actualPage = categoryServiceImpl.getAll(pageable);

        assertNotNull(actualPage);
        assertTrue(actualPage.isEmpty());
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Get a category by a valid ID should return the correct DTO")
    void getCategoryById_WithValidId_ShouldReturnCategoryDto() {
        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto actual = categoryServiceImpl.getCategoryById(VALID_CATEGORY_ID);

        assertEquals(categoryResponseDto, actual);
    }

    @Test
    @DisplayName("Get a category by an invalid ID should throw EntityNotFoundException")
    void getCategoryById_WithInvalidId_ShouldThrowException() {
        when(categoryRepository.findById(INVALID_CATEGORY_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryServiceImpl.getCategoryById(INVALID_CATEGORY_ID)
        );
        assertEquals("Category not found with id: " + INVALID_CATEGORY_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Get books by a valid category ID should return a page of book DTOs")
    void getBooksByCategoryId_WithValidId_ShouldReturnBookPage() {
        when(categoryRepository.existsById(VALID_CATEGORY_ID)).thenReturn(true);

        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);
        when(bookRepository.findAllByCategories_Id(VALID_CATEGORY_ID, pageable))
                .thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        Page<BookDtoWithoutCategoryIds> actualPage = categoryServiceImpl
                .getBooksByCategoryId(VALID_CATEGORY_ID, pageable);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(bookDtoWithoutCategoryIds, actualPage.getContent().get(0));
    }

    @Test
    @DisplayName("Update a category with a valid ID should return the updated DTO")
    void updateCategory_WithValidId_ShouldReturnUpdatedDto() {
        CreateCategoryRequestDto updateRequestDto = createCategoryUpdateRequestDto();
        CategoryResponseDto expectedResponseDto = createUpdatedCategoryDto(VALID_CATEGORY_ID);
        Category categoryFromDb = createDefaultCategory();

        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(
                Optional.of(categoryFromDb));
        when(categoryRepository.save(categoryFromDb)).thenReturn(categoryFromDb);
        when(categoryMapper.toDto(categoryFromDb)).thenReturn(expectedResponseDto);

        CategoryResponseDto actual = categoryServiceImpl.updateCategory(VALID_CATEGORY_ID,
                updateRequestDto);

        assertNotNull(actual);
        assertEquals(expectedResponseDto, actual);
        verify(categoryMapper).updateFromCategoryDto(updateRequestDto, categoryFromDb);
        verify(categoryRepository).save(categoryFromDb);
    }

    @Test
    @DisplayName("Update a category with an invalid ID should throw EntityNotFoundException")
    void updateCategory_WithInvalidId_ShouldThrowException() {
        when(categoryRepository.findById(INVALID_CATEGORY_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryServiceImpl.updateCategory(INVALID_CATEGORY_ID,
                        createCategoryRequestDto)
        );
        assertEquals("Category not found with id: " + INVALID_CATEGORY_ID, exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete a category by a valid ID should call deleteById")
    void deleteCategory_WithValidId_ShouldCallRepository() {
        when(categoryRepository.existsById(VALID_CATEGORY_ID)).thenReturn(true);
        categoryServiceImpl.deleteCategory(VALID_CATEGORY_ID);
        verify(categoryRepository).deleteById(VALID_CATEGORY_ID);
    }

    @Test
    @DisplayName("Delete a category by an invalid ID should complete without error")
    void deleteCategory_WithInvalidId_ShouldCompleteWithoutError() {
        when(categoryRepository.existsById(INVALID_CATEGORY_ID)).thenReturn(false);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryServiceImpl.deleteCategory(INVALID_CATEGORY_ID)
        );
        assertEquals("Cannot delete category. No category found with id: "
                + INVALID_CATEGORY_ID, exception.getMessage());
        verify(categoryRepository, never()).deleteById(any());
    }
}
