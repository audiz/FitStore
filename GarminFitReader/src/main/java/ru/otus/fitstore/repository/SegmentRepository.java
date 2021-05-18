package ru.otus.fitstore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.fitstore.domain.Segment;

@Repository
public interface SegmentRepository extends MongoRepository<Segment, String> {
}