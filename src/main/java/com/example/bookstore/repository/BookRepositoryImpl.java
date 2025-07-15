package com.example.bookstore.repository;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.CreateBookRequestDto;
import com.example.bookstore.exception.DataProcessingException;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.model.Book;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toModel(bookDto);
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(book);
            transaction.commit();
            Book savedBook = book;
            return bookMapper.toDto(savedBook);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't save book into DB " + book, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Book> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT b FROM Book b ", Book.class)
                    .getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get all books from DB", e);
        }
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.createQuery(
                            "FROM Book b WHERE b.id = :id", Book.class)
                    .setParameter("id", id)
                    .uniqueResultOptional()
                    .orElseThrow(() ->
                            new EntityNotFoundException("Book not found with id: " + id));
            return Optional.of(book);
        } catch (Exception e) {
            throw new DataProcessingException("Can't get book with id " + id, e);
        }
    }
}
