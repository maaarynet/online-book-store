package com.example.bookstore.repository.book.specification;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    private static final String ISBN = "isbn";

    @Override
    public Specification<Book> getSpecification(String[] isbns) {
        return (root, query, criteriaBuilder)
                -> root.get(ISBN).in(Arrays.stream(isbns).toArray());
    }

    @Override
    public String getKeys() {
        return ISBN;
    }
}
