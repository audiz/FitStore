package ru.otus.fitstore.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.fitstore.domain.Segment;
import ru.otus.fitstore.domain.SegmentInfo;
import ru.otus.fitstore.domain.TrainingData;
import ru.otus.fitstore.repository.SegmentRepository;
import ru.otus.fitstore.repository.TrainingDataRepository;
import ru.otus.fitstore.service.SegmentService;

import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@RestController
public class SegmentController {

    private final SegmentRepository segmentRepository;
    private final TrainingDataRepository trainingDataRepository;

    private final SegmentService segmentService;

    @GetMapping("segment/all")
    public ResponseEntity<List<Segment>> segment() {
        return ResponseEntity.ok(segmentRepository.findAll());
    }

    @GetMapping("segment/{id}")
    public ResponseEntity<Segment> segment(@PathVariable("id") String id) {
        return ResponseEntity.of(segmentRepository.findByIdUser(id));
    }

    @GetMapping("segment/id/{id}")
    public ResponseEntity<List<SegmentInfo>> segmentTest(@PathVariable("id") String id) {
        Optional<TrainingData> optionalTrainingData = trainingDataRepository.findById(id);
        if(optionalTrainingData.isEmpty()){
            throw new RuntimeException("Training data not found");
        }
        TrainingData trainingData = optionalTrainingData.get();
        List<Segment> segmentList = segmentRepository.findByLocation(trainingData.getGeoPolygon());
        return ResponseEntity.ok(segmentService.comparePaths(segmentList, trainingData));
    }

}
