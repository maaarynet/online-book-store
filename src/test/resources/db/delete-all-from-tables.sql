-- Step 1: Delete from all JOIN tables first.
-- These tables have foreign keys pointing to the main tables.
DELETE FROM books_categories;
DELETE FROM users_roles;
DELETE FROM cart_items;
DELETE FROM order_items;

-- Step 2: Now that the links are gone, delete from the tables that had items in the join tables.
DELETE FROM shopping_carts;
DELETE FROM orders;
DELETE FROM books;
DELETE FROM categories;

-- Step 3: Finally, delete from the remaining primary tables.
DELETE FROM users;
DELETE FROM roles;