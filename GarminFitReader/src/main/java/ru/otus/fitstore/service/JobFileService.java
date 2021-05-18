package ru.otus.fitstore.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.otus.fitstore.config.JobConfig;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class JobFileService {
    private final static String UPLOAD_PATH = "/tmp";

    private final JobOperator jobOperator;
    //@Qualifier("asyncJobLauncher")
    private final JobLauncher jobLauncher;
    private final Job importGarminFitDataJob;


    public Optional<String> handleFileWithJob(MultipartFile multipartFile, String userId) {
        Path directory = Paths.get(UPLOAD_PATH);
        String name = null;

        try {
            name = DigestUtils.md5Hex(multipartFile.getInputStream());
            InputStream inputStream = multipartFile.getInputStream();

            if(!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            long copy = Files.copy(inputStream, directory.resolve(name));

            return Optional.of(runJob(directory, name, copy, userId));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.of("upload failed");
        } finally {
            try {
                if(name != null && Files.exists(directory.resolve(name))) {
                    Files.delete(directory.resolve(name));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private String runJob(Path directory, String name, long copy, String userId) {
        Long jobId = null;
        String status = null;
        JobExecution execution = null;
        try {
            execution = jobLauncher.run(importGarminFitDataJob, new JobParametersBuilder()
                    .addString(JobConfig.INPUT_FILE_PATH, directory.resolve(name).toString())
                    .addString(JobConfig.USER_ID, userId)
                    .toJobParameters());

            log.debug("JOB getStatus: {}", execution.getStatus());
            jobId = execution.getId();
            status = execution.getStatus().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Job already exists";
        }
        return "Uploaded successfully, size: " + copy + ", job id = " + jobId + ", status = " + status;
    }
}
