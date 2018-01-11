package com.jakduk.batch.processor;

import com.jakduk.batch.common.rabbitmq.RabbitMQPublisher;
import com.jakduk.batch.model.db.Mail;
import com.jakduk.batch.model.db.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Locale;

@Slf4j
public class SendBulkMailProcessor implements ItemProcessor<User, User> {

    @Autowired private RabbitMQPublisher rabbitMQPublisher;

    private JobExecution jobExecution;

    @Override
    public User process(User item) {

        if (StringUtils.endsWithAny(item.getEmail(), "@jakduk.com", "@tfbnw.net"))
            return null;

        log.info("item=" + item);

        Mail mail = (Mail) jobExecution.getExecutionContext().get("mail");

        rabbitMQPublisher.sendBulk(Locale.KOREA, mail.getTemplateName(), mail.getSubject(), item.getEmail(),
                 new HashMap<String, Object>() {
                    {
                        put("username", item.getUsername());
                    }
                });

        return item;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecution = stepExecution.getJobExecution();
    }

}
