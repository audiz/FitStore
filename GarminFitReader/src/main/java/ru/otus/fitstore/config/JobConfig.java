package ru.otus.fitstore.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.fitstore.domain.TrainingData;
import ru.otus.fitstore.feign.SegmentFinderProxy;
import ru.otus.fitstore.repository.SegmentInfosRepository;
import ru.otus.fitstore.repository.SegmentRepository;
import ru.otus.fitstore.repository.TrainingDataRepository;
import ru.otus.fitstore.service.FitDecoder;
import ru.otus.fitstore.service.TransformService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class JobConfig {
    private static final int CHUNK_SIZE = 5;

    public static final String LAUNCH_TIME = "launchTime";
    public static final String IMPORT_GARMIN_DATA_JOB_NAME = "importGarminFitDataJob";
    public static final String INPUT_FILE_PATH = "inputFilePath";
    public static final String USER_ID = "userId";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job importGarminDataJob(Step stepMigrateData, Step stepHandleData) {
        return jobBuilderFactory.get(IMPORT_GARMIN_DATA_JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(stepMigrateData)
                .next(stepHandleData)
                .build();
    }

    @Bean
    public Step stepMigrateData(ItemWriter<TrainingData> trainingWriter, ItemReader<TrainingData> fitReader, ItemProcessor<TrainingData, TrainingData> processorTraining) {
        return stepBuilderFactory.get("stepMigrateData")
                .<TrainingData, TrainingData> chunk(CHUNK_SIZE)
                .reader(fitReader)
                .processor(processorTraining)
                .writer(trainingWriter)
                .build();
    }

    @Bean
    public Step stepHandleData(ItemWriter<TrainingData> monoTrainingItemWriter, MongoItemReader<TrainingData> mongoItemReader) {
        return stepBuilderFactory.get("stepHandleData")
                .<TrainingData, TrainingData> chunk(CHUNK_SIZE)
                .reader(mongoItemReader)
                .writer(monoTrainingItemWriter)
                .build();
    }

    @Bean
    public MongoItemWriter<TrainingData> trainingWriter(final MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<TrainingData>().template(mongoTemplate).collection("trainings").build();
    }

    @StepScope
    @Bean
    public TrainingDataReader fitReader(final FitDecoder fitDecoder,
                                     @Value("#{jobParameters['" + INPUT_FILE_PATH + "']}") String inputFilePath,
                                     @Value("#{jobParameters['" + USER_ID + "']}") String userId) {
        return new TrainingDataReader(inputFilePath, userId, fitDecoder);
    }

    @StepScope
    @Bean
    public MongoTrainingItemWriter monoTrainingItemWriter(final SegmentFinderProxy feignProxy, final TrainingDataRepository trainingDataRepository,
                                                          final SegmentInfosRepository segmentInfosRepository, final SegmentRepository segmentRepository) {
        return new MongoTrainingItemWriter(feignProxy, trainingDataRepository, segmentInfosRepository, segmentRepository);
    }

    @StepScope
    @Bean
    public ItemProcessor<TrainingData, TrainingData> processorTraining(TransformService transformService) {
        return transformService::transformData;
    }

    @StepScope
    @Bean
    public MongoItemReader<TrainingData> mongoItemReader(final MongoTemplate mongoTemplate,
                                  @Value("#{jobParameters['" + USER_ID + "']}") String userId) {
        MongoItemReader<TrainingData> reader = new MongoItemReader<TrainingData>();
        reader.setTemplate(mongoTemplate);
        reader.setCollection("trainings");
        reader.setTargetType(TrainingData.class);

        List<Object> param = new ArrayList<>();
        param.add(userId);
        reader.setParameterValues(param);
        reader.setQuery("{\"user.$id\" : ObjectId(\"?0\"), \"segmentInfoList\": null}");

        Map<String, Sort.Direction> sorts = new HashMap<>(1);
        sorts.put("date", Sort.Direction.DESC);
        reader.setSort(sorts);
        return reader;
    }

}
