package com.example.demo_redis.repositories;

import com.example.demo_redis.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User findFirstByEmail(String email);
}
