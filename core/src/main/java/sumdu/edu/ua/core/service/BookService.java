package sumdu.edu.ua.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.exception.NotFoundException;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.util.Set;

@Service
public class BookService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "author", "publishedAt");

    // 1. Ін'єкція залежності через конструктор (Constructor Injection)
    private final CatalogRepositoryPort catalogRepository;

    // 2. Ін'єкція залежності через поле (Field Injection) для демонстрації у Лаб 4
    @Autowired
    private CatalogAuditComponent auditComponent;

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
        if (auditComponent != null) {
            auditComponent.recordAccess("SEARCH_BOOKS", "q=" + query + ", page=" + page + ", size=" + size);
        }
        PageRequest pageRequest = new PageRequest(page, size, normalizedSort);
        return catalogRepository.findBooks(query, pageRequest);
    }

    public Book getBookById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("ID книги має бути додатним числом");
        }
        if (auditComponent != null) {
            auditComponent.recordAccess("GET_BOOK_BY_ID", "id=" + id);
        }
        return catalogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Книгу з ID " + id + " не знайдено"));
    }
}
