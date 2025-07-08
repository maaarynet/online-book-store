package com.example.bookstore;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(BookService bookService) {
        return args -> {
            bookService.save(new Book(
                    "Clean Code",
                    "Robert C. Martin",
                    "9780132350884",
                    new BigDecimal("37.59"),
                    "A book that tells how to write clean code.",
                    "https://example.com/cleancode.jpg"
            ));

            bookService.save(new Book(
                    "Effective Java",
                    "Joshua Bloch",
                    "9780134685991",
                    new BigDecimal("26.43"),
                    "Best practices for the Java platform.",
                    "https://example.com/effectivejava.jpg"
            ));

            System.out.println("Books in the store:");
            bookService.findAll().forEach(System.out::println);
        };
    }

}
