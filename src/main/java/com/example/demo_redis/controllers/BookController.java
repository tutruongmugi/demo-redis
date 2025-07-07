package com.example.demo_redis.controllers;

import com.example.demo_redis.models.Book;
import com.example.demo_redis.models.Category;
import com.example.demo_redis.repositories.BookRepository;
import com.example.demo_redis.repositories.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.SearchResult;

import java.util.*;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class BookController {

    private UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${app.autoCompleteKey}")
    private String autoCompleteKey;

    @Value("${app.booksSearchIndexName}")
    private String searchIndexName;

    @Autowired
    private ObjectMapper mapper;

    @GetMapping("/categories")
    public Iterable<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{isbn}")
    public Book get(@PathVariable("isbn") String isbn) {
        return bookRepository.findById(isbn).get();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> all( //
                                                    @RequestParam(defaultValue = "0") Integer page, //
                                                    @RequestParam(defaultValue = "10") Integer size //
    ) {
        Pageable paging = PageRequest.of(page, size);
        Page<Book> pagedResult = bookRepository.findAll(paging);
        List<Book> books = pagedResult.hasContent() ? pagedResult.getContent() : Collections.emptyList();

        Map<String, Object> response = new HashMap<>();
        response.put("books", books);
        response.put("page", pagedResult.getNumber());
        response.put("pages", pagedResult.getTotalPages());
        response.put("total", pagedResult.getTotalElements());

        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/authors")
    public List<String> authorAutoComplete(@RequestParam(name="q")String query) {
        return jedis.ftSugGet(autoCompleteKey, query, true, 20);
    }

    @GetMapping("/search")
    @Cacheable("book-search")
    public List<Book> search(@RequestParam(name="q")String query) {
        SearchResult searchResult = jedis.ftSearch(searchIndexName, query);
        List<Book> books = searchResult.getDocuments().stream()
                        .map(doc -> {
                            Map<String, Object> propertiesMap = new HashMap<>();
                            for (Map.Entry<String, Object> entry : doc.getProperties()) {
                                propertiesMap.put(entry.getKey(), entry.getValue());
                            }
                            return mapper.convertValue(propertiesMap, Book.class);
                        })
                        .toList();
        return books;
    }
}