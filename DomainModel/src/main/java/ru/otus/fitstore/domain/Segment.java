package ru.otus.fitstore.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "segments")
@Data
public class Segment {
    @Id
    private String id;
    private String name;
    private GeoLocation location;
    private Double distance;
    @DBRef(lazy = true)
    private List<SegmentInfo> segmentInfoList;
    private List<SegmentInfo> topSegmentInfoList;
    private Long lowestTopTime;

    private List<User> userInfos;
}
