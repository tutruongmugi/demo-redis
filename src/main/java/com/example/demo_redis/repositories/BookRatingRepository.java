package com.example.demo_redis.repositories;

import com.example.demo_redis.models.BookRating;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRatingRepository extends CrudRepository<BookRating, String> {
}
