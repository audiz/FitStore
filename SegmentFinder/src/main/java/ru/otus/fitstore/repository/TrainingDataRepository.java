package ru.otus.fitstore.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.fitstore.domain.TrainingData;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingDataRepository extends MongoRepository<TrainingData, String> {
    @Query(value = "{}", fields = "{ 'records' : 0, 'archive' : 0 }")
    List<TrainingData> findAll();

    @Query(value = "{}", fields = "{ 'records' : 0, 'archive' : 0 }")
    List<TrainingData> findAll(Sort sort);

    @Override
    Optional<TrainingData> findById(String id);
}