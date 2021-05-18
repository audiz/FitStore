package ru.otus.example.facadegateway.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.fitstore.domain.User;

public interface UserRepository extends MongoRepository<User, String> {
    @Query(value="{ 'username' : :#{#userName} }")
    User findByUsername(@Param("userName") String userName);
}