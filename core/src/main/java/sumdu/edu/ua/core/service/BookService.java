package sumdu.edu.ua.core.service;

import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.exception.NotFoundException;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.util.Set;

public class BookService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "author", "publishedAt");
    private final CatalogRepositoryPort catalogRepository;

    public BookService(CatalogRepositoryPort catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public Page<Book> searchBooks(String query, int page, int size, String sort) {
        if (page < 0) {
            throw new ValidationException("Параметр 'page' не може бути від'ємним");
        }
        if (size < 1 || size > 100) {
            throw new ValidationException("Параметр 'size' має бути в діапазоні від 1 до 100");
        }
        String normalizedSort = (sort == null || sort.isBlank()) ? "id" : sort.trim();
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSort)) {
            throw new ValidationException("Недопустиме поле сортування: " + normalizedSort);
        }
        PageRequest pageRequest = new PageRequest(page, size, normalizedSort);
        return catalogRepository.findBooks(query, pageRequest);
    }

    public Book getBookById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("ID книги має бути додатним числом");
        }
        return catalogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Книгу з ID " + id + " не знайдено"));
    }
}
