package ru.otus.fitstore.domain;

import lombok.Data;

import java.util.List;

@Data
public class GeoLocation {
    String type = "LineString";
    List<Double[]> coordinates;
}
