package ru.otus.fitstore.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Record implements Serializable {
    Short heartRate;
    Integer lat;
    Integer lon;
    Short cadence;
    Byte temperature; // C
    Double distance; // m
    Double speed; // m/s
    Double altitude; // m
    Double grade; // %
    Date timestamp;
}
