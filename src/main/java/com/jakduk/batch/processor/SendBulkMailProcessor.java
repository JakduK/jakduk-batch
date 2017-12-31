package com.jakduk.batch.processor;

import com.jakduk.batch.configuration.JakdukProperties;
import com.jakduk.batch.model.db.Mail;
import com.jakduk.batch.model.db.User;
import com.jakduk.batch.model.rabbitmq.SendBulkMailRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Locale;

@Slf4j
public class SendBulkMailProcessor implements ItemProcessor<User, User> {

    @Resource private JakdukProperties.ApiServer apiServerProperties;

    @Autowired private RestTemplate restTemplate;

    private JobExecution jobExecution;

    @Override
    public User process(User item) {

        if (StringUtils.endsWithAny(item.getEmail(), "@jakduk.com", "@tfbnw.net"))
            return null;

        // 테스트 용도
        if (! "phjang1983@daum.net".equals(item.getEmail()))
            return null;

        log.info("item=" + item);

        Mail mail = (Mail) jobExecution.getExecutionContext().get("mail");

        UriComponents sendBulkMailUriComponents = UriComponentsBuilder.fromHttpUrl(apiServerProperties.getServerUrl())
                .path(apiServerProperties.getSendBulkMail())
                .build();

        RequestEntity sendBulkMailRequestEntity = RequestEntity.post(sendBulkMailUriComponents.toUri())
                .body(SendBulkMailRequest.builder()
                        .locale(Locale.KOREA)
                        .templateName(mail.getTemplateName())
                        .subject(mail.getSubject())
                        .recipientEmail(item.getEmail())
                        .body(
                                new HashMap<String, Object>() {
                                    {
                                        put("username", item.getUsername());
                                    }
                                }
                        )
                        .build());

//        restTemplate.exchange(sendBulkMailRequestEntity, String.class);

        return null;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecution = stepExecution.getJobExecution();
    }

}
