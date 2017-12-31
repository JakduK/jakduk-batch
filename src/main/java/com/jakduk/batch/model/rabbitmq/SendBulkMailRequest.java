package com.jakduk.batch.model.rabbitmq;

import lombok.Builder;
import lombok.Getter;

import java.util.Locale;
import java.util.Map;

/**
 * Created by pyohwanjang on 2017. 6. 17..
 */

@Builder
@Getter
public class SendBulkMailRequest {
    private Locale locale;
    private String type;
    private String templateName;
    private String recipientEmail;
    private String subject;
    private Map<String, Object> body;
}
