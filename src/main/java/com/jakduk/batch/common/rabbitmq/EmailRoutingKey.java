package com.jakduk.batch.common.rabbitmq;

import lombok.Getter;

/**
 * Created by Jang,Pyohwan on 2017. 6. 15..
 */

@Getter
public enum EmailRoutingKey {

    EMAIL_BULK("email-bulk");

    private String routingKey;

    EmailRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

}
