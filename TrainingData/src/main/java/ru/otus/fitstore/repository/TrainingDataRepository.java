package ru.otus.fitstore.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.fitstore.domain.TrainingData;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingDataRepository extends MongoRepository<TrainingData, String> {
    @Query(value = "{}", fields = "{ 'records' : 0, 'archive' : 0 }")
    List<TrainingData> findAll();

    @Query(value = "{}", fields = "{ 'records' : 0, 'archive' : 0 }")
    List<TrainingData> findAll(Sort sort);

    @Aggregation(pipeline = {
            "{ $match: { _id: new ObjectId('?0') }  }",

            "{\n" +
                    "     $lookup:{\n" +
                    "         from:\"segmentInfos\",\n" +
                    "         localField:\"segmentInfoList.$id\",\n" +
                    "         foreignField:\"_id\",\n" +
                    "         as:\"segmentInfos\"\n" +
                    "     }\n" +
                    " }",

            "{\n" +
                    "        \"$addFields\": {\n" +
                    "            \"segmentInfos\": {\n" +
                    "                \"$map\": {\n" +
                    "                    \"input\": \"$segmentInfos\",\n" +
                    "                    \"as\": \"row\",\n" +
                    "                    \"in\": {\n" +
                    "                        \"id\": { $toObjectId: \"$$row._id\" },\n" +
                    "                        \"segmentId\": { $toObjectId: \"$$row.segmentId\" } \n" +
                    "                      \n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }",

            "{\n" +
                    "     $lookup:{\n" +
                    "         from:\"segments\",\n" +
                    "         localField:\"segmentInfos.segmentId\",\n" +
                    "         foreignField:\"_id\",\n" +
                    "         as:\"segmentInfos\"\n" +
                    "     }\n" +
                    "}",

            "{\n" +
                    "        \"$addFields\": {\n" +
                    "            \"segmentInfos\": {\n" +
                    "                \"$map\": {\n" +
                    "                    \"input\": \"$segmentInfos\",\n" +
                    "                    \"as\": \"row\",\n" +
                    "                    \"in\": {\n" +
                    "                        \"_id\": { $toObjectId: \"$$row._id\" },\n" +
                    "                        \"name\": \"$$row.name\"\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }"

    })
    Optional<TrainingData> findByIdSegment(String id);
}