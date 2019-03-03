package com.jakduk.batch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/jobs")
public class JobLauncherController {

    @Autowired private JobLauncher jobLauncher;
    @Autowired private Job sendBulkMailJob;
    @Autowired private Job initElasticsearchIndexJob;
    @Autowired private Job removeOldGalleryJob;

    @PostMapping("send-bulk-mail/{id}")
    public EmptyJsonResponse sendBulkMail(@PathVariable String id)
            throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        JobParameters parameters = new JobParametersBuilder()
                .addString("mail.id", id)
                .addDate("date", new Date())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(sendBulkMailJob, parameters);

        this.checkStatus(jobExecution.getStatus());

        return EmptyJsonResponse.newInstance();
    }

    @PostMapping("init-elasticsearch-index")
    public EmptyJsonResponse initElasticsearchIndex()
            throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        JobParameters parameters = new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(initElasticsearchIndexJob, parameters);

        this.checkStatus(jobExecution.getStatus());

        return EmptyJsonResponse.newInstance();
    }

    @PostMapping("remove-old-gallery")
    public EmptyJsonResponse removeOldGalleryJob()
            throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        JobParameters parameters = new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(removeOldGalleryJob, parameters);

        this.checkStatus(jobExecution.getStatus());

        return EmptyJsonResponse.newInstance();
    }

    private void checkStatus(BatchStatus batchStatus) {
        if (batchStatus.isUnsuccessful())
            throw new RuntimeException("Job이 실패했습니다. Status=" + batchStatus.getBatchStatus());
    }
}
