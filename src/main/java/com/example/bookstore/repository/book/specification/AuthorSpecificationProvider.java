package com.example.bookstore.repository.book.specification;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String AUTHOR = "author";

    @Override
    public Specification<Book> getSpecification(String[] authors) {
        return (root,query, criteriaBuilder)
                -> root.get(AUTHOR).in(Arrays.stream(authors).toArray());
    }

    @Override
    public String getKeys() {
        return AUTHOR;
    }
}
