package sumdu.edu.ua.persistence.adapter;

import org.springframework.stereotype.Component;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.persistence.entity.BookEntity;
import sumdu.edu.ua.persistence.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaCatalogRepositoryAdapter implements CatalogRepositoryPort {
    private final BookRepository bookRepository;

    public JpaCatalogRepositoryAdapter(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Book save(Book book) {
        BookEntity entity = (book.getId() != null)
                ? bookRepository.findById(book.getId()).orElse(new BookEntity())
                : new BookEntity();
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setYear(book.getYear());
        entity.setDescription(book.getDescription());
        BookEntity saved = bookRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    private Book toDomain(BookEntity e) {
        return new Book(e.getId(), e.getTitle(), e.getAuthor(), e.getYear(), e.getDescription());
    }
}
