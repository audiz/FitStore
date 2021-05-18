package ru.otus.fitstore.domain;

import lombok.Data;
import java.util.List;

@Data
public class GeoPolygon {
    String type = "Polygon";
    List<List<Double[]>> coordinates;
}
