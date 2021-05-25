package ru.otus.fitstore.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.fitstore.domain.Record;
import ru.otus.fitstore.domain.TrainingData;
import ru.otus.fitstore.repository.TrainingDataRepository;
import ru.otus.fitstore.service.ArchiveService;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
public class TrainingDataController {

    private final TrainingDataRepository trainingDataRepository;
    private final ArchiveService archiveService;

    @GetMapping("data/all")
    public ResponseEntity<List<TrainingData>> all() {
        List<TrainingData> list = trainingDataRepository.findAll(Sort.by(Sort.Direction.DESC, "addedDate")); //date
        for(TrainingData trainingData: list) {
            String polyline = archiveService.unzip(trainingData.getPolyData());
            trainingData.setPolyUrl(polyline);
            trainingData.setPolyData(null);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("data/findbyid/{id}")
    public ResponseEntity<TrainingData> findById(@PathVariable String id){

        TrainingData trainingData = trainingDataRepository.findByIdSegment(id).get();
        if(trainingData.getArchive() != null) {
            List<Record> records = archiveService.unzip(trainingData.getArchive());
            trainingData.setRecords(records);
            trainingData.setArchive(null);
        }
        return ResponseEntity.ok(trainingData);
    }
}
