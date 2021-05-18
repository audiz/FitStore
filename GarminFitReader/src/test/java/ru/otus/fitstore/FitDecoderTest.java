package ru.otus.fitstore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.fitstore.domain.TrainingData;
import ru.otus.fitstore.service.FitDecoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Класс FitDecoder")
public class FitDecoderTest {

    FitDecoder fitDecoder = new FitDecoder();

    @Test
    void testDecoder() {
        TrainingData trainingData = fitDecoder.decode("src/test/resources/test.fit");
        assertEquals(60, trainingData.getRecords().size());
    }
}
