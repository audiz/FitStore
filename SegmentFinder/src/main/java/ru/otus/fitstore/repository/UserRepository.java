package ru.otus.fitstore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.fitstore.domain.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query(value="{ username: :#{#username} }")
    User findByUsername(String username);
}