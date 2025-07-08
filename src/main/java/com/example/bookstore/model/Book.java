package com.example.bookstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String title;

    @NotNull
    private String author;

    @NotNull
    @Column(unique = true)
    private String isbn;

    @NotNull
    private BigDecimal price;

    private String description;
    private String coverImage;

    public Book(String title, String author, String isbn, BigDecimal bigDecimal,
                String description, String coverImage) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = bigDecimal;
        this.description = description;
        this.coverImage = coverImage;
    }

    public Book() {

    }
}
