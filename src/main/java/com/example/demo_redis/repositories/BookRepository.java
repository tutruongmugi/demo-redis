package com.example.demo_redis.repositories;

import com.example.demo_redis.models.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, String>, CrudRepository<Book, String> {
}
