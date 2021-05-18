package ru.otus.fitstore.domain;

import lombok.Data;

@Data
public class GarminSession {
    Double maxAltitude; // 353.2
    Short maxCadence;
    Short maxHeartRate;
    Double maxSpeed; // 11.359
    Byte maxTemperature;

    Double avgAltitude; // 297.4
    Short avgCadence;
    Short avgHeartRate;
    Double avgSpeed; // 6.759
    Byte avgTemperature;

    Integer totalAscent;
    Integer totalDescent;
    Double totalDistance; // 23367.98
    Double totalMovingTime; // 3458.0
    Double totalElapsedTime; // 3540.0
}
