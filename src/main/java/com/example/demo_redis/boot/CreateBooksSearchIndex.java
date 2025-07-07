package com.example.demo_redis.boot;

import com.example.demo_redis.models.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.schemafields.SchemaField;
import redis.clients.jedis.search.schemafields.TextField;

@Component
@Order(6)
@Slf4j
public class CreateBooksSearchIndex implements CommandLineRunner {

    private UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");

    @Value("${app.booksSearchIndexName}")
    private String searchIndexName;

    @Override
    public void run(String... args) throws Exception {
        if (jedis.ftList().contains(searchIndexName)) {
            log.info("Books search index already exist");
            return;
        }

        log.info(">>>> Creating Books Search Index...");
        SchemaField[] schema = {
                TextField.of("title").sortable(),
                TextField.of("subTitle"),
                TextField.of("description"),
        };

        String createResult = jedis.ftCreate(searchIndexName,
            FTCreateParams.createParams()
                    .on(IndexDataType.HASH)
                    .addPrefix(Book.class.getName() + ":"),
            schema
        );

        log.info(">>>> Created Books Search Index...");
    }
}
