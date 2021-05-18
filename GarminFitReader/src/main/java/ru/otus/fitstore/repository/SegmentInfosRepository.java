package ru.otus.fitstore.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.fitstore.domain.SegmentInfo;
import ru.otus.fitstore.domain.TrainingData;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegmentInfosRepository extends MongoRepository<SegmentInfo, String> {
}