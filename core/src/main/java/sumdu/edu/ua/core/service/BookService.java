package sumdu.edu.ua.core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.exception.NotFoundException;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookService {
    private final CatalogRepositoryPort catalogRepository;

    public BookService(CatalogRepositoryPort catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return catalogRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("ID книги має бути додатним числом");
        }
        return catalogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Книгу з ID " + id + " не знайдено"));
    }

    @Transactional
    public Book save(Book book) {
        if (book == null) {
            throw new ValidationException("Дані книги не передані");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new ValidationException("Назва книги не може бути порожньою");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new ValidationException("Автор книги не може бути порожнім");
        }
        int year = (book.getYear() != null) ? book.getYear() : LocalDate.now().getYear();
        if (year < 1000 || year > LocalDate.now().getYear() + 1) {
            throw new ValidationException("Некоректний рік видання: " + year);
        }
        book.setTitle(book.getTitle().trim());
        book.setAuthor(book.getAuthor().trim());
        book.setYear(year);
        if (book.getDescription() == null) {
            book.setDescription("");
        }
        return catalogRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        Book existing = findById(id);
        catalogRepository.deleteById(existing.getId());
    }
}
