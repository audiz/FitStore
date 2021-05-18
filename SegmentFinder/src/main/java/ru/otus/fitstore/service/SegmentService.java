package ru.otus.fitstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.fitstore.domain.*;
import ru.otus.fitstore.domain.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SegmentService {
    private final static int CONVERT_GEO_COORDS = 11930465;
    private final ArchiveService archiveService;

    // 0.0001 ~ for 10 meters
    // 0.00001 ~ for 1 meter
    public static final double APPROXIMATE_DISTANCE = 0.00025;

    public List<SegmentInfo> comparePaths(List<Segment> segmentList, TrainingData trainingData) {
        List<Record> records = archiveService.unzip(trainingData.getArchive());

        int recordsSize = records.size();
        List<SegmentInfo> segmentInfoList = new ArrayList<>();

        for(Segment segment: segmentList) {
            List<Double[]> segmentPath = segment.getLocation().getCoordinates();
            boolean hasApproximate = false;
            int segmentSize = segmentPath.size();
            int segmentApproxCount = 0;
            int recordI = 0;
            Date startDate = null;
            Date endDate = null;
            int prevRecordI = 0;
            List<Double> speed = new ArrayList<>();
            List<Short> heart = new ArrayList<>();

            for(int segmentI = 0; segmentI < segmentSize; segmentI++) {
                Double[] coords = segmentPath.get(segmentI);
                Double lat = coords[0];
                Double lon = coords[1];
               // log.info("Segment coords = {} {}", lat, lon);

                for(int i = recordI; i < recordsSize; i ++) {
                    if(hasApproximateCoords(lat, lon, records.get(i).getLat(), records.get(i).getLon())) {
                        hasApproximate = true;
                        if(startDate == null) {
                            startDate = records.get(i).getTimestamp();
                        }
                        segmentApproxCount++;
                        //log.info("Has approximate");
                        recordI = i;
                        endDate = records.get(i).getTimestamp();
                        speed.add(records.get(i).getSpeed());
                        heart.add(records.get(i).getHeartRate());
                        break;
                    }
                }
                // try to find real start point, by skipping parts of track
                if(hasApproximate && prevRecordI != 0 && (recordI - prevRecordI) > 20 ) {
                    log.info("segment fail segmentI = {}, prevRecordI = {}, recordI = {}", segmentI, prevRecordI, recordI);
                    speed.clear();
                    segmentApproxCount = 0;
                    hasApproximate = false;
                    startDate = null;
                    // restart segment
                    segmentI = -1;
                }
                prevRecordI = recordI;
            }

            double diffPercent = (100 - segmentApproxCount * 100 / segmentSize);
            log.info("segmentApproxCount = {}, segmentSize = {}, segmentName = {}, prc = {}", segmentApproxCount, segmentSize, segment.getName(), diffPercent);

            // approximate equality diff < 5%
            if(diffPercent < 5) { //segmentApproxCount == segmentSize

                Long tookTime = null;
                if(endDate != null && startDate != null) {
                    tookTime = (endDate.getTime() - startDate.getTime()) / 1000;
                }

                double avgSpeed = speed.stream().mapToDouble(value -> value).average().getAsDouble();
                double maxSpeed = speed.stream().mapToDouble(value -> value).max().getAsDouble();

                short avgHeart = (short) heart.stream().mapToInt(value -> value).average().getAsDouble();
                short maxHeart = (short) heart.stream().mapToInt(value -> value).max().getAsInt();

                log.info("tookTime = {} sec, speed = {}, maxSpeed = {}, avgHeart = {}, maxHeart = {}",
                        tookTime, avgSpeed * 3.6, maxSpeed * 3.6, avgHeart, maxHeart);
                //log.info("startDate = {}, endDate = {}", startDate, endDate);

                SegmentInfo segmentInfo = new SegmentInfo();
                segmentInfo.setAvgHeart(avgHeart);
                segmentInfo.setMaxHeart(maxHeart);
                segmentInfo.setAvgSpeed(avgSpeed);
                segmentInfo.setMaxSpeed(maxSpeed);
                segmentInfo.setDate(startDate);
                segmentInfo.setTime(tookTime);
                segmentInfo.setUserId(trainingData.getUser().getId());
                segmentInfo.setTrainingId(trainingData.getId());
                segmentInfo.setSegmentId(segment.getId());

                segmentInfoList.add(segmentInfo);
            }
        }
        return segmentInfoList;
    }

    private boolean hasApproximateCoords(Double lat, Double lon, Integer lat1, Integer lon1) {

        if(lat1 != null && lon1 != null) {
            double rLat = (double) lat1 / CONVERT_GEO_COORDS;
            double rLon = (double) lon1 / CONVERT_GEO_COORDS;

            return rLat <= lat + APPROXIMATE_DISTANCE && rLat >= lat - APPROXIMATE_DISTANCE && rLon <= lon + APPROXIMATE_DISTANCE && rLon >= lon - APPROXIMATE_DISTANCE;
        }
        return false;
    }
}
