package com.example.bookstore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.book.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    private static final Long SCI_FI_CATEGORY_ID = 101L;
    private static final Long NON_EXISTENT_CATEGORY_ID = 99L;
    private static final String DUNE_TITLE = "Dune";
    private static final String FOUNDATION_TITLE = "Foundation";
    private static final int EXPECTED_SCI_FI_BOOK_COUNT = 2;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 5;

    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @Autowired
    private BookRepository bookRepository;

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }

    @Test
    @DisplayName("findAllByCategories_Id: Positive case -"
            + " should return books for an existing category")
    @Sql(
            scripts = "classpath:db/book/add-books-for-category-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:db/delete-all-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findAllByCategories_Id_WhenCategoryHasBooks_ShouldReturnPageOfBooks() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        Page<Book> actualPage = bookRepository.findAllByCategories_Id(SCI_FI_CATEGORY_ID, pageable);

        assertThat(actualPage.getContent())
                .hasSize(EXPECTED_SCI_FI_BOOK_COUNT)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder(DUNE_TITLE, FOUNDATION_TITLE);
    }

    @Test
    @DisplayName("findAllByCategories_Id: Negative case "
            + "- should return an empty page for a non-existent category")
    @Sql(
            scripts = "classpath:db/book/add-books-for-category-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:db/delete-all-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findAllByCategories_Id_WhenCategoryDoesNotExist_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        Page<Book> actualPage = bookRepository.findAllByCategories_Id(NON_EXISTENT_CATEGORY_ID,
                pageable);

        assertThat(actualPage).isEmpty();
    }
}
