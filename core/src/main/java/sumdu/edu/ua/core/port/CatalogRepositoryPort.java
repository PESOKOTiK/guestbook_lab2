package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.Book;

import java.util.List;
import java.util.Optional;

public interface CatalogRepositoryPort {
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book save(Book book);
    void deleteById(Long id);
}
