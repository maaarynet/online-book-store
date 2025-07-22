package com.example.bookstore.repository.book.specification;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationprovider implements SpecificationProvider<Book> {
    private static final String TITLE = "title";

    @Override
    public Specification<Book> getSpecification(String[] titles) {
        return (root,query, criteriaBuilder)
                -> root.get(TITLE).in(Arrays.stream(titles).toArray());
    }

    @Override
    public String getKeys() {
        return TITLE;
    }
}
