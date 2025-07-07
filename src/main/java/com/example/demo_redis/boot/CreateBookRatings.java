package com.example.demo_redis.boot;

import com.example.demo_redis.models.Book;
import com.example.demo_redis.models.BookRating;
import com.example.demo_redis.models.User;
import com.example.demo_redis.repositories.BookRatingRepository;
import com.example.demo_redis.repositories.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.IntStream;

@Component
@Order(4)
@Slf4j
public class CreateBookRatings implements CommandLineRunner {
    @Value("${app.numberOfRatings}")
    private Integer numberOfRatings;

    @Value("${app.ratingStars}")
    private Integer ratingStars;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private BookRatingRepository bookRatingRepo;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookRatingRepository bookRatingRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bookRatingRepo.count() != 0){
            return;
        }

        Random random = new Random();
        IntStream.range(0, numberOfRatings).forEach(n -> {
            int stars = random.nextInt(ratingStars) + 1;
            String bookId = redisTemplate.opsForSet().randomMember(Book.class.getName());
            String userId = redisTemplate.opsForSet().randomMember(User.class.getName());

            Book book = new Book();
            book.setId(bookId);

            User user = new User();
            user.setId(userId);

            BookRating bookRating = BookRating.builder()
                    .book(book)
                    .user(user)
                    .rating(stars)
                    .build();
            bookRatingRepository.save(bookRating);
        });

        log.info(">>> Booking created...");
    }
}
