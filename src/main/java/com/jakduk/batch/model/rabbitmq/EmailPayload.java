package com.jakduk.batch.model.rabbitmq;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Locale;
import java.util.Map;

/**
 * Created by pyohwanjang on 2017. 6. 17..
 */

@Builder
@Getter
@ToString
public class EmailPayload {
    private Locale locale;
    private String type;
    private String templateName;
    private String recipientEmail;
    private String subject;
    private Map<String, String> extra;
    private Map<String, Object> body;
}
