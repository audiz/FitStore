package ru.otus.fitstore.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import ru.otus.fitstore.domain.Segment;
import ru.otus.fitstore.domain.SegmentInfo;
import ru.otus.fitstore.domain.TrainingData;
import ru.otus.fitstore.feign.SegmentFinderProxy;
import ru.otus.fitstore.repository.SegmentInfosRepository;
import ru.otus.fitstore.repository.SegmentRepository;
import ru.otus.fitstore.repository.TrainingDataRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class MongoTrainingItemWriter implements ItemWriter<TrainingData> {

    public static final int SEGMENT_TOP_SIZE = 10;

    private final SegmentFinderProxy feignProxy;
    private final TrainingDataRepository trainingDataRepository;
    private final SegmentInfosRepository segmentInfosRepository;
    private final SegmentRepository segmentRepository;

    @Override
    public void write(List<? extends TrainingData> items) throws Exception {
        for (TrainingData trainingData: items) {
            List<SegmentInfo> segmentInfoList =  feignProxy.findSegments(trainingData.getId());

            segmentInfoList = segmentInfosRepository.saveAll(segmentInfoList);
            trainingData.setSegmentInfoList(segmentInfoList);

            for(SegmentInfo segmentInfo: segmentInfoList) {
                Optional<Segment> segmentOptional = segmentRepository.findById(segmentInfo.getSegmentId());
                if(segmentOptional.isPresent()) {
                    Segment segment = segmentOptional.get();
                    List<SegmentInfo> segmentTopInfos = segment.getTopSegmentInfoList();

                    if(segment.getLowestTopTime() == null || segment.getLowestTopTime() < segmentInfo.getTime() || segmentTopInfos.size() < SEGMENT_TOP_SIZE) {
                        // sort segments for top results only
                        if(segmentTopInfos == null) {
                            segmentTopInfos = new ArrayList<>();
                        }
                        segmentTopInfos.add(segmentInfo);
                        segmentTopInfos = segmentTopInfos.stream().sorted(Comparator.comparingLong(SegmentInfo::getTime)).limit(10).collect(Collectors.toList());
                        segment.setTopSegmentInfoList(segmentTopInfos);
                        long lowestTime = segmentTopInfos.stream().mapToLong(SegmentInfo::getTime).min().orElse(0);
                        segment.setLowestTopTime(lowestTime);
                        segmentRepository.save(segment);
                    }
                }
            }

            trainingDataRepository.save(trainingData);
        }
    }
}
