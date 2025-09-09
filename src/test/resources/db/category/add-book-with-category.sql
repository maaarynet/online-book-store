-- Insert a category with ID=1 that matches createDefaultCategoryResponseDto
INSERT INTO categories (id, name, description, is_deleted)
VALUES (1, 'Horror', 'Horror books', false);

-- Insert a book with data that matches createDefaultBookWithoutCategoryIdsDto
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 10.99, 'A novel about the American dream.', 'http://example.com/cover.jpg', false);

-- Link the book to the category
INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1);