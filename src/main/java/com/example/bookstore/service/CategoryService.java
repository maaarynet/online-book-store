package com.example.bookstore.service;

import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryResponseDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponseDto createCategory(CreateCategoryRequestDto bookDto);

    Page<CategoryResponseDto> getAll(Pageable pageable);

    CategoryResponseDto getCategoryById(Long id);

    Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id, Pageable pageable);

    CategoryResponseDto updateCategory(Long id, CreateCategoryRequestDto bookDto);

    void deleteCategory(Long id);
}
