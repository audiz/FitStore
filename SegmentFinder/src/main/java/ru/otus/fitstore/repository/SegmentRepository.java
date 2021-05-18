package ru.otus.fitstore.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.fitstore.domain.GeoPolygon;
import ru.otus.fitstore.domain.Segment;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegmentRepository extends MongoRepository<Segment, String> {
    @Query(value = "{ location: { $geoWithin: { $geometry: :#{#polygon} }}}")
    List<Segment> findByLocation(@Param("polygon") GeoPolygon polygon);

    @Aggregation(pipeline = {
            "{ $match: { _id: new ObjectId('?0') }  }",

            "{ $addFields: { \n" +
                    "            userInfos: {\n" +
                    "              $map:\n" +
                    "                 {\n" +
                    "                    \"input\": \"$topSegmentInfoList\",\n" +
                    "                    \"as\": \"row\",\n" +
                    "                    \"in\": {\n" +
                    "                        \"id\": \"$$row.$id\",\n" +
                    "                        \"userId\": { $toObjectId: \"$$row.userId\" },\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "         }}",

            "{ $lookup: {\n" +
                    "         from:\"users\",\n" +
                    "         localField:\"userInfos.userId\",\n" +
                    "         foreignField:\"_id\",\n" +
                    "         as:\"userInfos\"\n" +
                    "     }}",

            "{ $addFields: {\n" +
                    "            \"userInfos\": {\n" +
                    "                \"$map\": {\n" +
                    "                    \"input\": \"$userInfos\",\n" +
                    "                    \"as\": \"row\",\n" +
                    "                    \"in\": {\n" +
                    "                        \"_id\": { $toObjectId: \"$$row._id\" },\n" +
                    "                        \"firstName\": \"$$row.firstName\",\n" +
                    "                        \"lastName\": \"$$row.lastName\",\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }}"
    })
    Optional<Segment> findByIdUser(String id);
}