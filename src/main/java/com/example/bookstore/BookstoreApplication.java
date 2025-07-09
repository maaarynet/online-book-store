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
            bookService.findAll();
            Book book = new Book();
            book.setTitle("Effective Java");
            book.setAuthor("Joshua Bloch");
            book.setIsbn("9780134685991");
            book.setPrice(BigDecimal.valueOf(26.43));
            bookService.save(book);
            System.out.println("Books in the store:");
            bookService.findAll().forEach(System.out::println);
        };
    }

}
