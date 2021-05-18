package ru.otus.fitstore.rest;

import io.leonard.PolylineUtils;
import io.leonard.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.otus.fitstore.domain.Record;
import ru.otus.fitstore.domain.TrainingData;
import ru.otus.fitstore.repository.TrainingDataRepository;
import ru.otus.fitstore.service.ArchiveService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

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

    // TODO edit \ remove

    // TODO explicit
    @GetMapping("data/polyline/{id}")
    public String polyline(@PathVariable String id) {
        TrainingData trainingData = trainingDataRepository.findById(id).get();
        final List<Position> path = new ArrayList<>();

        if(trainingData.getArchive() != null) {
            List<Record> records = archiveService.unzip(trainingData.getArchive());
            trainingData.setRecords(records);
            trainingData.setArchive(null);
        }


        int size = trainingData.getRecords().size();
        //System.out.println(size);
        int skipSize = size / 300;
        int i = 0;
        for(Record record: trainingData.getRecords()) {
            if(record.getLon() != null && record.getLon() != 0) {
                if(i == 0) {
                    path.add(Position.fromLngLat((float) record.getLon() / 11930465, (float) record.getLat() / 11930465));
                }
                i++;
                if(i > skipSize) {
                    i = 0;
                }
            }
        }
        //path.add(Position.fromLngLat(-120.2, 38.5));

        String encoded = PolylineUtils.encode(path, 5);
        return "https://maps.googleapis.com/maps/api/staticmap?size=544x218&maptype=roadmap&path=color:0x000000ff|weight:2|enc:"+encoded+"&format=jpg&key=AIzaSyABkqwr_0fTkKCOvzE_5CFpo4gneLgR2-I";
    }
}
