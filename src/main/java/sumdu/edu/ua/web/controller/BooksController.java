package sumdu.edu.ua.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

/**
 * Spring MVC controller for books catalog web interface.
 * Demonstrates constructor-based dependency injection.
 */
@Controller
public class BooksController {

    private final CatalogRepositoryPort bookRepo;

    /**
     * Constructor-based dependency injection.
     * Spring will automatically inject the CatalogRepositoryPort bean.
     */
    @Autowired
    public BooksController(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    @GetMapping("/books")
    public String listBooks(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sort,
            Model model) {

        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 20;
        }

        PageRequest pageRequest = new PageRequest(page, size, sort, true);
        Page<Book> result = bookRepo.search(q, pageRequest);
        long total = result.getTotal();
        int totalPages = (int) ((total + size - 1) / size);

        model.addAttribute("books", result.getItems());
        model.addAttribute("q", q);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", totalPages);

        return "books";
    }
}

