package com.example.demo_redis.repositories;

import com.example.demo_redis.models.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, String> {
    Role findFirstByName(String role);
}
