package com.example.demo_redis.boot;

import com.example.demo_redis.repositories.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.UnifiedJedis;

@Component
@Order(7)
@Slf4j
public class CreateAuthorNameSuggestions implements CommandLineRunner {
    private UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");

    @Autowired
    private BookRepository bookRepository;

    @Value("${app.autoCompleteKey}")
    private String autoCompleteKey;

    @Override
    public void run(String... args) throws Exception {
        if (jedis.ftSugGet(autoCompleteKey, "*") != null) {
            log.info("Key already exist");
            return;
        }

        log.info(">>>> Creating Author Name Suggestions...");

        bookRepository.findAll().forEach(book -> {
            if (book.getAuthors() != null) {
                book.getAuthors().forEach(author -> {
                    jedis.ftSugAdd(autoCompleteKey, author, 1.0);
                });
            }
        });

        log.info(">>>> Created Author Name Suggestions...");
    }
}
