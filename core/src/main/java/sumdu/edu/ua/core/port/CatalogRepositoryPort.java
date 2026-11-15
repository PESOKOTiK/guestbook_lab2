package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;

import java.util.Optional;

public interface CatalogRepositoryPort {
    Page<Book> findBooks(String query, PageRequest pageRequest);
    Optional<Book> findById(Long id);
    Book save(Book book);
}
