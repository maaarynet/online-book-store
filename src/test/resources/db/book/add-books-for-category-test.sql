INSERT INTO categories (id, name, description) VALUES (100, 'Fiction', 'Classic fiction novels');
INSERT INTO categories (id, name, description) VALUES (101, 'Science Fiction', 'Futuristic and speculative fiction');

INSERT INTO books (id, title, author, isbn, price, description, is_deleted) VALUES (101, 'The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 10.99, 'A classic American novel set in the Jazz Age, exploring themes of wealth, love, and the American Dream', false);
INSERT INTO books (id, title, author, isbn, price, description, is_deleted) VALUES (102, 'Dune', 'Frank Herbert', '978-0441013593', 12.99, 'Epic science fiction novel set on the desert planet Arrakis, featuring political intrigue and ecological themes', false);
INSERT INTO books (id, title, author, isbn, price, description, is_deleted) VALUES (103, 'Foundation', 'Isaac Asimov', '978-0553803719', 11.50, 'First book in the Foundation series, depicting the fall of a galactic empire and the science of psychohistory', false);

INSERT INTO books_categories (book_id, category_id) VALUES (101, 100);
INSERT INTO books_categories (book_id, category_id) VALUES (102, 101);
INSERT INTO books_categories (book_id, category_id) VALUES (103, 101);