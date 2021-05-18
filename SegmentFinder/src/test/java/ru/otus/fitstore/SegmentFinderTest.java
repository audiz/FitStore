package ru.otus.fitstore;

import com.github.cloudyrock.spring.v5.EnableMongock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.fitstore.domain.Segment;
import ru.otus.fitstore.domain.SegmentInfo;
import ru.otus.fitstore.domain.TrainingData;
import ru.otus.fitstore.domain.User;
import ru.otus.fitstore.repository.SegmentRepository;
import ru.otus.fitstore.repository.TrainingDataRepository;
import ru.otus.fitstore.repository.UserRepository;
import ru.otus.fitstore.service.SegmentService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@EnableMongock
@Slf4j
@SpringBootTest
public class SegmentFinderTest {

    @Autowired
    private SegmentService segmentService;

    @Autowired
    private SegmentRepository segmentRepository;

    @Autowired
    private TrainingDataRepository trainingDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindSegment() {
        Optional<Segment> segmentOptional = segmentRepository.findById("60919b6149752dc7a82cdd43");
        assertTrue(segmentOptional.isPresent());

        Optional<TrainingData> optionalTrainingData = trainingDataRepository.findById("60a3f317c876ca363f0b8009");
        assertTrue(optionalTrainingData.isPresent());

        Optional<User> optionalUser = userRepository.findById("608c33645402f1671194b4c2");
        assertTrue(optionalUser.isPresent());

        TrainingData trainingData = optionalTrainingData.get();
        trainingData.setUser(optionalUser.get());

        List<SegmentInfo> segmentInfoList = segmentService.comparePaths(List.of(segmentOptional.get()), trainingData);
        assertEquals(1, segmentInfoList.size());
    }

    // up and down record - should find too
    @Test
    void shouldFindUpDownSegment() {
        Optional<Segment> segmentOptional = segmentRepository.findById("60919b6149752dc7a82cdd43");
        assertTrue(segmentOptional.isPresent());

        Optional<TrainingData> optionalTrainingData = trainingDataRepository.findById("60a3f337c876ca363f0b800b");
        assertTrue(optionalTrainingData.isPresent());

        Optional<User> optionalUser = userRepository.findById("608c33645402f1671194b4c2");
        assertTrue(optionalUser.isPresent());

        TrainingData trainingData = optionalTrainingData.get();
        trainingData.setUser(optionalUser.get());

        List<SegmentInfo> segmentInfoList = segmentService.comparePaths(List.of(segmentOptional.get()), trainingData);
        assertEquals(1, segmentInfoList.size());
    }

    @Test
    void shouldNotFindSegment() {
        Optional<Segment> segmentOptional = segmentRepository.findById("60919b6149752dc7a82cdd43");
        assertTrue(segmentOptional.isPresent());

        Optional<TrainingData> optionalTrainingData = trainingDataRepository.findById("60a3f35dc876ca363f0b800c");
        assertTrue(optionalTrainingData.isPresent());

        Optional<User> optionalUser = userRepository.findById("608c33645402f1671194b4c2");
        assertTrue(optionalUser.isPresent());

        TrainingData trainingData = optionalTrainingData.get();
        trainingData.setUser(optionalUser.get());

        List<SegmentInfo> segmentInfoList = segmentService.comparePaths(List.of(segmentOptional.get()), trainingData);
        assertEquals(0, segmentInfoList.size());
    }
}
