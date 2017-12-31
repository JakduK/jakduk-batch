package com.jakduk.batch.common.rabbitmq;

import com.jakduk.batch.configuration.JakdukProperties;
import com.jakduk.batch.model.rabbitmq.EmailPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Map;

/**
 * Created by pyohwanjang on 2017. 6. 17..
 */

@Slf4j
@Component
public class RabbitMQPublisher {

    private final String QUEUE_EMAIL = "email";
    private final String EMAIL_TYPE_BULK = "BULK";

    @Resource private JakdukProperties.Rabbitmq rabbitmqProperties;

    @Autowired private RabbitTemplate rabbitTemplate;

    public void sendBulk(Locale locale, String templateName, String subject, String recipientEmail, Map<String, Object> body) {
        EmailPayload emailPayload = EmailPayload.builder()
                .locale(locale)
                .type(EMAIL_TYPE_BULK)
                .templateName(templateName)
                .recipientEmail(recipientEmail)
                .subject(subject)
                .body(body)
                .build();

        String routingKey = rabbitmqProperties.getRoutingKeys().get(EmailRoutingKey.EMAIL_BULK.getRoutingKey());
        this.publishEmail(routingKey, emailPayload);
    }

    private void publishEmail(String routingKey, EmailPayload message) {
        if (rabbitmqProperties.getQueues().get(QUEUE_EMAIL).getEnabled()) {
            rabbitTemplate.convertAndSend(rabbitmqProperties.getExchangeName(), routingKey, message);
        } else {
            log.info("Can not publish message. {} queue is disabled.", QUEUE_EMAIL);
        }
    }

}
