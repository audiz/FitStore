package ru.otus.fitstore.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "segmentInfos")
@Data
public class SegmentInfo {
    @Id
    private String id;
    private Long time;
    private Double avgSpeed;
    private Double maxSpeed;
    private Short avgHeart;
    private Short maxHeart;
    private Date date;
    private String userId;
    private String segmentId;
    private String trainingId;
}
