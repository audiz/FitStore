package ru.otus.fitstore.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import ru.otus.fitstore.domain.TrainingData;
import ru.otus.fitstore.domain.User;
import ru.otus.fitstore.service.FitDecoder;

import java.util.ArrayList;

@Slf4j
public class TrainingDataReader extends AbstractItemCountingItemStreamItemReader<TrainingData>
{
    private final String pathToFitFile;
    private final FitDecoder fitDecoder;
    private final String userId;

    public TrainingDataReader(String pathToFitFile, String userId, FitDecoder fitDecoder) {
        this.pathToFitFile = pathToFitFile;
        this.fitDecoder = fitDecoder;
        this.userId = userId;
    }

    @Override
    protected void doClose() throws Exception
    {
        setMaxItemCount(1);
        setCurrentItemCount(0);
    }

    @Override
    protected void doOpen() throws Exception
    {
        setName("trainingDataReader");
        setMaxItemCount(1);
    }

    @Override
    protected TrainingData doRead() throws Exception
    {
        TrainingData trainingData = fitDecoder.decode(pathToFitFile);

        if(trainingData.getRecords().size() == 0) {
            log.error("getRecords is empty");
            trainingData = null;
        }
        log.debug("TrainingData read from file = '{}'", pathToFitFile);

        User user = new User();
        user.setId(userId);

        trainingData.setUser(user);

        return trainingData;
    }
}