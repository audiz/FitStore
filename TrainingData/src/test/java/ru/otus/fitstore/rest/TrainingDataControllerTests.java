package ru.otus.fitstore.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cloudyrock.spring.v5.EnableMongock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTypeExcludeFilter;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.fitstore.domain.TrainingData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// From @DataMongoTest
@ExtendWith({SpringExtension.class})
@OverrideAutoConfiguration(
        enabled = false
)
@TypeExcludeFilters({DataMongoTypeExcludeFilter.class})
@AutoConfigureCache
@AutoConfigureDataMongo
@ImportAutoConfiguration
// From @DataMongoTest

@EnableMongock
@EnableConfigurationProperties
@ComponentScan({"ru.otus.fitstore"})
@WebMvcTest(TrainingDataController.class)
public class TrainingDataControllerTests {
    @Autowired
    private MockMvc mvc;

    @DisplayName("должен вернуть лист с 1 записью из базы /data/all")
    @Test
    public void testGetAllApi() throws Exception {
        MvcResult result =  mvc.perform(get("/data/all")).andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<TrainingData> myObjects = mapper.readValue(content, mapper.getTypeFactory().constructCollectionType(List.class, TrainingData.class));

        assertThat(myObjects.size()).isEqualTo(1);
    }
/*
    @DisplayName("должен вернуть 1 запись по айди /data/findbyid/{id}")
    @Test
    public void testGetByIdApi() throws Exception {
        MvcResult result =  mvc.perform(get("/data/findbyid/6099944bde96a471eb517377")).andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        TrainingData myObjects = mapper.readValue(content, TrainingData.class);

        assertThat(myObjects).isNotNull();
    }*/

}
