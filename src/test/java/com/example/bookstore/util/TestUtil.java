package com.example.bookstore.util;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.BookSearchParametersDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.category.CategoryResponseDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestUtil {
    public static Book createDefaultBook() {
        return new Book()
                .setId(1L)
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(BigDecimal.valueOf(10.99))
                .setDescription("A novel about the American dream.")
                .setCoverImage("http://example.com/cover.jpg")
                .setCategories(Set.of(createDefaultCategory()));
    }

    public static List<BookDto> createListOfTwoBookDtos() {
        BookDto gatsbyDto = new BookDto()
                .setId(1L)
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(new BigDecimal("15.99"))
                .setDescription(null)
                .setCoverImage(null)
                .setCategoryIds(new ArrayList<>());

        BookDto mockingbirdDto = new BookDto()
                .setId(2L)
                .setTitle("To Kill a Mockingbird")
                .setAuthor("Harper Lee")
                .setIsbn("978-0061120084")
                .setPrice(new BigDecimal("12.50"))
                .setDescription(null)
                .setCoverImage(null)
                .setCategoryIds(new ArrayList<>());

        return List.of(gatsbyDto, mockingbirdDto);
    }

    public static Category createDefaultCategory() {
        return new Category()
                .setId(1L)
                .setName("Fiction");
    }

    public static BookDto createDefaultBookDto() {
        return new BookDto()
                .setId(1L)
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(new BigDecimal("15.99"))
                .setDescription("A story of the fabulously wealthy Jay Gatsby.")
                .setCoverImage(null)
                .setCategoryIds(new ArrayList<>());
    }

    public static CreateBookRequestDto createDefaultBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(BigDecimal.valueOf(10.99))
                .setDescription("A novel about the American dream.")
                .setCoverImage("http://example.com/cover.jpg")
                .setCategoryIds(List.of(1L));
    }

    public static CreateBookRequestDto createInvalidRequestBookDto() {
        return new CreateBookRequestDto()
                .setTitle(" ")
                .setAuthor(" ")
                .setIsbn(" ")
                .setPrice(BigDecimal.valueOf(-10.99))
                .setDescription(" ")
                .setCoverImage(" ")
                .setCategoryIds(List.of(1L));

    }

    public static CreateBookRequestDto createBookUpdateRequestDto() {
        return createDefaultBookRequestDto()
                .setTitle("A New Updated Title");
    }

    public static BookDto createUpdatedBookDto(Long id) {
        return createDefaultBookDto()
                .setId(id)
                .setTitle("A New Updated Title");
    }

    public static BookSearchParametersDto createBookSearchDtoByAuthor(String... author) {
        return new BookSearchParametersDto(null, author, null);
    }

    public static List<BookDto> createBooksForAuthorSearch() {
        BookDto gatsbyDto = new BookDto()
                .setId(1L)
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(new BigDecimal("15.99"))
                .setDescription("A story of the American dream.")
                .setCoverImage(null)
                .setCategoryIds(new ArrayList<>());

        BookDto tenderDto = new BookDto()
                .setId(3L)
                .setTitle("Tender Is the Night")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0684801544")
                .setPrice(new BigDecimal("14.99"))
                .setDescription("A novel by F. Scott Fitzgerald.")
                .setCoverImage(null)
                .setCategoryIds(new ArrayList<>());

        return List.of(gatsbyDto, tenderDto);
    }

    public static CategoryResponseDto createDefaultCategoryResponseDto() {
        return new CategoryResponseDto()
                .setId(1L)
                .setName("Horror")
                .setDescription("Horror books");
    }

    public static CreateCategoryRequestDto createDefaultCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName("Biography")
                .setDescription("Biography books");
    }

    public static CreateCategoryRequestDto createInvalidRequestCategoryDto() {
        return new CreateCategoryRequestDto()
                .setName("")
                .setDescription("");
    }

    public static BookDtoWithoutCategoryIds createDefaultBookWithoutCategoryIdsDto() {
        return new BookDtoWithoutCategoryIds()
                .setId(1L)
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(BigDecimal.valueOf(10.99))
                .setDescription("A novel about the American dream.")
                .setCoverImage("http://example.com/cover.jpg");
    }

    public static CreateCategoryRequestDto createCategoryUpdateRequestDto() {
        return createDefaultCategoryRequestDto()
                .setName("A New Updated Category Name")
                .setDescription("A New Updated Category Description");
    }

    public static CategoryResponseDto createUpdatedCategoryDto(Long id) {
        return createDefaultCategoryResponseDto()
                .setId(id)
                .setName("A New Updated Category Name")
                .setDescription("A New Updated Category Description");
    }

    public static List<CategoryResponseDto> createListOfTwoCategoryDtos() {
        CategoryResponseDto horrorDto = new CategoryResponseDto()
                .setId(1L)
                .setName("Horror")
                .setDescription("Horror books");

        CategoryResponseDto biographyDto = new CategoryResponseDto()
                .setId(2L)
                .setName("Biography")
                .setDescription("Biography books");

        return List.of(horrorDto, biographyDto);
    }
}
