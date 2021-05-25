package ru.otus.fitstore.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.otus.fitstore.config.JobConfig;
import ru.otus.fitstore.service.JobFileService;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@RestController
public class ConvertController {

    private final JobExplorer jobExplorer;

    //@Qualifier("asyncJobLauncher")
    private final JobLauncher jobLauncher;

    private final Job importGarminFitDataJob;
    private final JobFileService jobFileService;

    @PostMapping("reader/uploadFile")
    public ResponseEntity<String> uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile multipartFile) {

        String userId = null;
        Iterator<String> iterator = request.getHeaders("userId").asIterator();
        if(iterator.hasNext()) {
            userId = iterator.next();
        }
        if(userId == null) {
            throw new RuntimeException("User not found");
        }
        log.debug("upload file user id = {}", userId);

        return ResponseEntity.of(jobFileService.handleFileWithJob(multipartFile, userId));
    }

    @GetMapping("reader/jobinfo/{jobId}")
    public ResponseEntity<String> jobInfo(@PathVariable("jobId") Long jobId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(jobId);
        if(jobExecution == null) {
            return ResponseEntity.ok("not found");
        }
        return ResponseEntity.ok(jobExecution.getStatus().toString());
    }
}
