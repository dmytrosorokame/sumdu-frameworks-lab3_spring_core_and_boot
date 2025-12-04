package sumdu.edu.ua.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.web.http.ErrorResponse;

/**
 * REST API controller for books.
 * Demonstrates constructor-based dependency injection for ObjectMapper (custom bean).
 */
@RestController
@RequestMapping("/api/books")
public class BooksApiController {

    private static final Logger log = LoggerFactory.getLogger(BooksApiController.class);

    private final CatalogRepositoryPort bookRepo;
    private final ObjectMapper objectMapper;

    /**
     * Constructor-based dependency injection.
     * Demonstrates injection of both repository and custom bean (ObjectMapper).
     */
    @Autowired
    public BooksApiController(CatalogRepositoryPort bookRepo, ObjectMapper objectMapper) {
        this.bookRepo = bookRepo;
        this.objectMapper = objectMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBooks(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sort) {

        if (page < 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                            "page must be >= 0", "/api/books"));
        }
        if (size <= 0 || size > 100) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                            "size must be between 1 and 100", "/api/books"));
        }

        try {
            PageRequest pageRequest = new PageRequest(page, size, sort, true);
            var result = bookRepo.search(q, pageRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("DB error while GET /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Internal Server Error", "DB error", "/api/books"));
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            if (book.getTitle() == null || book.getTitle().isBlank()
                    || book.getAuthor() == null || book.getAuthor().isBlank()) {
                log.warn("Bad request: title & author required");
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "title & author required", "/api/books"));
            }

            if (book.getPubYear() <= 0) {
                log.warn("Bad request: invalid pubYear");
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "invalid pubYear", "/api/books"));
            }

            Book saved = bookRepo.add(
                    book.getTitle().trim(),
                    book.getAuthor().trim(),
                    book.getPubYear()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            log.error("DB error while POST /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Internal Server Error", "DB error", "/api/books"));
        }
    }
}

