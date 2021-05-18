package ru.otus.fitstore.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "trainings")
@Data
public class TrainingData {
    @Id
    private String id;
    private String title;
    private String description;
    private Date date;
    private Date addedDate;
    @Transient
    private List<Record> records;
    private byte[] archive;
    private GarminSession garminSession;
    private GeoPolygon geoPolygon;
    private HrZones hrZones;
    @Transient
    private String polyUrl;
    private byte[] polyData;
    @DBRef
    private User user;
    @DBRef
    private List<SegmentInfo> segmentInfoList;
    //for get_by_id aggregation
    private List<Segment> segmentInfos;
}
