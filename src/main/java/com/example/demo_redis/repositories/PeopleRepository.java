package com.example.demo_redis.repositories;

import com.example.demo_redis.models.Person;
import com.redis.om.spring.repository.RedisDocumentRepository;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;


public interface PeopleRepository extends RedisDocumentRepository<Person, String>, CrudRepository<Person, String> {
    Iterable<Person> findByHomeLocNear(Point lon, Distance distance);

    Iterable<Person> findByAddress_City(String city);
    Iterable<Person> findBySkills(Set<String> skills);
}
