package ru.otus.fitstore.service;

import io.leonard.PolylineUtils;
import io.leonard.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.fitstore.domain.*;
import ru.otus.fitstore.repository.UserRepository;

import ru.otus.fitstore.domain.Record;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
@Service
public class TransformService {
    // it seems Garmin stores its angular coordinates using a 32-bit integer, so that gives 2^32 possible values.
    // We want to be able to represent values up to 360° (or -180 to 180), so each degree represents 2^32 / 360 = 11930465.
    private final static int CONVERT_GEO_COORDS = 11930465;
    // + 50 meters for geo bounds
    public static final double SEGMENT_ADDITION = 0.0005;
    public static final int TAKE_EVERY_N_RECORD = 300;
    public static final int GOOGLE_POLYLINE_PRECISION = 5;


    private final UserRepository userRepository;
    private final ArchiveService archiveService;

    public Integer getUserMaxHeartRate(TrainingData trainingData) {
        Integer userMaxHeartRate = null;
        Optional<User> optionalUser = userRepository.findById(trainingData.getUser().getId());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            //log.info("user = {}", user);
            userMaxHeartRate = Integer.valueOf(user.getMaxHeartRate());
        }
        return userMaxHeartRate;
    }

    public TrainingData transformData(TrainingData trainingData) {
        Integer minLon = null;
        Integer maxLon = null;
        Integer minLat = null;
        Integer maxLat = null;

        int total = 0;
        Integer[] zones = new Integer[]{ 0, 0, 0, 0, 0 };
        Integer userMaxHeartRate = getUserMaxHeartRate(trainingData);

        List<Position> path = new ArrayList<>();
        int size = trainingData.getRecords().size();

        final int skipSize = size < TAKE_EVERY_N_RECORD * 2 ? 2 : size / TAKE_EVERY_N_RECORD;

        Date firstDate = trainingData.getRecords().stream().map(Record::getTimestamp).findFirst().orElse(new Date());

        path = IntStream.range(0, trainingData.getRecords().size()).filter(n -> n % skipSize == 0)
                .mapToObj(trainingData.getRecords()::get)
                .filter(record -> record.getLon() != null && record.getLon() != 0)
                .map(record -> Position.fromLngLat((float) record.getLon() / CONVERT_GEO_COORDS, (float) record.getLat() / CONVERT_GEO_COORDS))
                .collect(Collectors.toList());

        for(Record record: trainingData.getRecords()) {

            // find zones
            if(userMaxHeartRate != null && record.getHeartRate() != null) {
                int zone = getZone(record.getHeartRate(), userMaxHeartRate);
                zones[zone]++;
                total++;
            }

            // find geo square
            if(record.getLon() != null) {
                if(maxLon == null || record.getLon() > maxLon) {
                    maxLon = record.getLon();
                }
                if(minLon == null || record.getLon() < minLon) {
                    minLon = record.getLon();
                }
                if(maxLat == null || record.getLat() > maxLat) {
                    maxLat = record.getLat();
                }
                if(minLat == null || record.getLat() < minLat) {
                    minLat = record.getLat();
                }
            }
        }

        // Create square geo polygon for mongodb
        if(minLon != null && maxLon != null && minLat != null && maxLat != null) {
            double minLon1 = (Double.valueOf(minLon) / CONVERT_GEO_COORDS) - SEGMENT_ADDITION;
            double maxLon1 = (Double.valueOf(maxLon) / CONVERT_GEO_COORDS) + SEGMENT_ADDITION;
            double minLat1 = (Double.valueOf(minLat) / CONVERT_GEO_COORDS) - SEGMENT_ADDITION;
            double maxLat1 = (Double.valueOf(maxLat) / CONVERT_GEO_COORDS) + SEGMENT_ADDITION;
            //        { lat: 57.2533110821749, lng: 60.086490845076 },
            // log.info("transformData  \n{ lat: {}, lng: {} },\n{ lat: {}, lng: {} },\n{ lat: {}, lng: {} },\n{ lat: {}, lng: {} },\n{ lat: {}, lng: {} }",
            //        maxLat1, minLon1, maxLat1, maxLon1, minLat1, maxLon1, minLat1, minLon1, maxLat1, minLon1);

            GeoPolygon geoPolygon = new GeoPolygon();
            geoPolygon.setCoordinates(new ArrayList<>());
            geoPolygon.getCoordinates().add(new ArrayList<>());
            geoPolygon.getCoordinates().get(0).add(new Double[] {maxLat1, minLon1});
            geoPolygon.getCoordinates().get(0).add(new Double[] {maxLat1, maxLon1});
            geoPolygon.getCoordinates().get(0).add(new Double[] {minLat1, maxLon1});
            geoPolygon.getCoordinates().get(0).add(new Double[] {minLat1, minLon1});
            geoPolygon.getCoordinates().get(0).add(new Double[] {maxLat1, minLon1});

            trainingData.setGeoPolygon(geoPolygon);
        }

        // set hr zones
        if(total != 0) {
            HrZones hrZones = new HrZones();
            hrZones.setZone1sec(zones[0]);
            hrZones.setZone2sec(zones[1]);
            hrZones.setZone3sec(zones[2]);
            hrZones.setZone4sec(zones[3]);
            hrZones.setZone5sec(zones[4]);
            hrZones.setMaxHr(userMaxHeartRate);
            trainingData.setHrZones(hrZones);
        }

        // save db size
        if(trainingData.getRecords() != null && trainingData.getRecords().size() > 0) {
            byte[] bytes = archiveService.zip(trainingData.getRecords());
            trainingData.setArchive(bytes);
        }
        // for google polyline
        if(path.size() > 0) {
            byte[] bytes = archiveService.zip(PolylineUtils.encode(path, GOOGLE_POLYLINE_PRECISION));
            trainingData.setPolyData(bytes);
        }

        trainingData.setDate(firstDate);
        trainingData.setAddedDate(new Date());

        String dayPart = getDayPartOfTime(firstDate);

        trainingData.setTitle(dayPart);

        return trainingData;
    }

    public String getDayPartOfTime(Date date) {
        String greeting = "";
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 4) greeting = "Night ride";
        else if (hour < 12) greeting = "Morning ride";
        else if (hour < 18) greeting = "Afternoon ride";
        else greeting = "Evening ride";
        return greeting;
    }

    public int getZone(int heartRate, int maxHeartRate) {
        if(heartRate < maxHeartRate *.6) {
            return 0;
        }
        if(heartRate < maxHeartRate *.7) {
            return 1;
        }
        if(heartRate < maxHeartRate *.8) {
            return 2;
        }
        if(heartRate < maxHeartRate *.9) {
            return 3;
        }
        return 4;
    }

}
