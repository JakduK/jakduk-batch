package com.jakduk.batch.controller;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * JSON 타입의 빈 객체
 *
 * @author pyohwan
 * 16. 7. 8 오전 12:55
 */

@JsonInclude
public class EmptyJsonResponse {

    private static EmptyJsonResponse emptyJsonResponse;

    public static EmptyJsonResponse newInstance() {

        if (Objects.isNull(emptyJsonResponse))
            emptyJsonResponse = new EmptyJsonResponse();

        return emptyJsonResponse;
    }
}
