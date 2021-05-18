package ru.otus.fitstore.service;

import com.garmin.fit.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.fitstore.domain.GarminSession;
import ru.otus.fitstore.domain.Record;
import ru.otus.fitstore.domain.TrainingData;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@Slf4j
@Component
public class FitDecoder {

    public TrainingData decode(String fitFile) {
        TrainingData trainingData = new TrainingData(); //read garmin data
        trainingData.setTitle("title");
        trainingData.setDescription("description");
        trainingData.setRecords(new ArrayList<>());

        Decode decode = new Decode();
        //decode.skipHeader();        // Use on streams with no header and footer (stream contains FIT defn and data messages only)
        //decode.incompleteStream();  // This suppresses exceptions with unexpected eof (also incorrect crc)
        MesgBroadcaster mesgBroadcaster = new MesgBroadcaster(decode);
        Listener listener = new Listener(trainingData);
        FileInputStream in;
        try {
            in = new FileInputStream(fitFile);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error opening file " + fitFile + " [1]");
        }

        try {
            if (!decode.checkFileIntegrity((InputStream)in)) {
                throw new RuntimeException("FIT file integrity failed.");
            }
        } catch (RuntimeException e) {
            log.error("Exception Checking File Integrity: ");
            log.error(e.getMessage());
            log.error("Trying to continue...");
        } finally {
            try {
                in.close();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            in = new FileInputStream(fitFile);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error opening file " + fitFile + " [2]");
        }

       // mesgBroadcaster.addListener((FileIdMesgListener)listener);
       // mesgBroadcaster.addListener((UserProfileMesgListener)listener);
      //  mesgBroadcaster.addListener((DeviceInfoMesgListener)listener);
        mesgBroadcaster.addListener((RecordMesgListener)listener);
        mesgBroadcaster.addListener((SessionMesgListener)listener);

        try {
            decode.read(in, mesgBroadcaster, mesgBroadcaster);
        } catch (FitRuntimeException e) {
            // If a file with 0 data size in it's header  has been encountered,
            // attempt to keep processing the file
            if (decode.getInvalidFileDataSize()) {
                decode.nextFile();
                decode.read(in, mesgBroadcaster, mesgBroadcaster);
            } else {
                log.error("Exception decoding file: ");
                log.error(e.getMessage());
                try {
                    in.close();
                } catch (java.io.IOException f) {
                    throw new RuntimeException(f);
                }
                return null;
            }
        }

        try {
            in.close();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        //System.out.println("Decoded FIT file " + fitFile + ".");
        log.debug("Decoded FIT file '{}'", fitFile);

        return trainingData;
    }

    private static class Listener implements SessionMesgListener, RecordMesgListener {
        TrainingData trainingData;

        public Listener(TrainingData trainingData) {
            this.trainingData = trainingData;
        }

        @Override
        public void onMesg(RecordMesg mesg) {
            Record record = new Record();
            record.setHeartRate(mesg.getHeartRate());
            record.setCadence(mesg.getCadence());

            if(mesg.getDistance() != null) {
                record.setDistance(BigDecimal.valueOf(mesg.getDistance()).setScale(2, RoundingMode.HALF_UP).doubleValue());  // 110.03
            }
            if(mesg.getSpeed() != null) {
                record.setSpeed(BigDecimal.valueOf(mesg.getSpeed()).setScale(3, RoundingMode.HALF_UP).doubleValue()); // 5.618
            }
            if(mesg.getAltitude() != null) {
                record.setAltitude(BigDecimal.valueOf(mesg.getAltitude()).setScale(1, RoundingMode.HALF_UP).doubleValue()); //318.4
            }

            record.setTemperature(mesg.getTemperature());
            record.setTimestamp(mesg.getTimestamp().getDate());

            if(mesg.getGrade() != null) {
                record.setGrade(BigDecimal.valueOf(mesg.getGrade()).setScale(2, RoundingMode.HALF_UP).doubleValue()); // 0.88
            }

            record.setLat(mesg.getPositionLat());
            record.setLon(mesg.getPositionLong());

            trainingData.getRecords().add(record);
        }

        @Override
        public void onMesg(SessionMesg mesg) {

            GarminSession garminSession = new GarminSession();

            if(mesg.getMaxAltitude() != null) {
                garminSession.setMaxAltitude(BigDecimal.valueOf(mesg.getMaxAltitude()).setScale(1, RoundingMode.HALF_UP).doubleValue());
            }
            garminSession.setMaxCadence(mesg.getMaxCadence());
            garminSession.setMaxHeartRate(mesg.getMaxHeartRate());

            if(mesg.getMaxSpeed() != null) {
                garminSession.setMaxSpeed(BigDecimal.valueOf(mesg.getMaxSpeed()).setScale(3, RoundingMode.HALF_UP).doubleValue());
            }
            garminSession.setMaxTemperature(mesg.getMaxTemperature());

            if(mesg.getAvgAltitude() != null) {
                garminSession.setAvgAltitude(BigDecimal.valueOf(mesg.getAvgAltitude()).setScale(1, RoundingMode.HALF_UP).doubleValue());
            }
            garminSession.setAvgCadence(mesg.getAvgCadence());
            garminSession.setAvgHeartRate(mesg.getAvgHeartRate());
            if(mesg.getAvgSpeed() != null) {
                garminSession.setAvgSpeed(BigDecimal.valueOf(mesg.getAvgSpeed()).setScale(3, RoundingMode.HALF_UP).doubleValue());
            }
            garminSession.setAvgTemperature(mesg.getAvgTemperature());

            garminSession.setTotalAscent(mesg.getTotalAscent());
            garminSession.setTotalDescent(mesg.getTotalDescent());
            if(mesg.getTotalDistance() != null) {
                garminSession.setTotalDistance(BigDecimal.valueOf(mesg.getTotalDistance()).setScale(2, RoundingMode.HALF_UP).doubleValue());
            }
            if(mesg.getTotalMovingTime() != null) {
                garminSession.setTotalMovingTime(BigDecimal.valueOf(mesg.getTotalMovingTime()).setScale(1, RoundingMode.HALF_UP).doubleValue());
            }
            if(mesg.getTotalElapsedTime() != null) {
                garminSession.setTotalElapsedTime(BigDecimal.valueOf(mesg.getTotalElapsedTime()).setScale(1, RoundingMode.HALF_UP).doubleValue());
            }

            trainingData.setGarminSession(garminSession);

        }
    }

}
