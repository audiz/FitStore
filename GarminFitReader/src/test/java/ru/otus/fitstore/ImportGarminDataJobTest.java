package ru.otus.fitstore;

import com.github.cloudyrock.spring.v5.EnableMongock;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.fitstore.config.JobConfig;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@EnableMongock
@SpringBootTest
@SpringBatchTest
public class ImportGarminDataJobTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Rule
    public WireMockRule wm = new WireMockRule();
    private WireMockServer wireMockServer;

    @BeforeEach
    void clearMetaData() {

        this.wireMockServer = new WireMockServer(
                options()
                        .extensions(new ResponseTemplateTransformer(false))
                        .port(18081));
        this.wireMockServer.stubFor(get(anyUrl())
                .willReturn(aResponse()
                        .withBody("[]")
                        .withHeader("Content-Type", "application/json")));
        this.wireMockServer.start();

        jobRepositoryTestUtils.removeJobExecutions();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testJob() throws Exception {

        Job job = jobLauncherTestUtils.getJob();
        Assertions.assertThat(job).isNotNull() .extracting(Job::getName).isEqualTo(JobConfig.IMPORT_GARMIN_DATA_JOB_NAME);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParametersBuilder()
                .addString(JobConfig.INPUT_FILE_PATH, "src/test/resources/test.fit")
                .addString(JobConfig.USER_ID, "608c33645402f1671194b4c2")
                .toJobParameters());

        Assertions.assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    }
}
