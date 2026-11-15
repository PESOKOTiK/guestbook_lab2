INSERT INTO books (title, author, pub_year, description) VALUES
('Clean Architecture', 'Robert C. Martin', 2017, 'A Craftsman''s Guide to Software Structure and Design'),
('The Art of Computer Programming', 'Donald E. Knuth', 1968, 'Fundamental Algorithms (Rare Classic Edition)'),
('Designing Data-Intensive Applications', 'Martin Kleppmann', 2017, 'The Big Ideas Behind Reliable, Scalable, and Maintainable Systems'),
('Design Patterns (GoF)', 'Erich Gamma et al.', 1994, 'Elements of Reusable Object-Oriented Software'),
('Effective Java', 'Joshua Bloch', 2018, 'Best practices for the Java platform');

INSERT INTO users (username, password, role) VALUES
('pesokotik', 'bcrypt_placeholder_1', 'ROLE_ADMIN'),
('evgenia', 'bcrypt_placeholder_2', 'ROLE_USER'),
('student_sumdu', 'bcrypt_placeholder_3', 'ROLE_USER');

INSERT INTO comments (book_id, user_id, text, created_at) VALUES
(1, 1, 'Чудова книга про розділення відповідальності та чисту архітектуру!', DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),
(3, 1, 'Обов''язкова до прочитання кожному бекенд-розробнику розподілених систем.', DATEADD('HOUR', -5, CURRENT_TIMESTAMP)),
(1, 2, 'Дуже зрозумілі приклади залежностей між модулями.', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
(2, 3, 'Фундаментальна класика алгоритмізації (старий коментар > 24 годин).', DATEADD('HOUR', -48, CURRENT_TIMESTAMP));
