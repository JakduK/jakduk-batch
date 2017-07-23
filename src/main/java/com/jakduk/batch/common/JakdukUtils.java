package com.jakduk.batch.common;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

public class JakdukUtils {

    /**
     * HTML TAG를 제거한다.
     */
    public static String stripHtmlTag(String htmlTag) {
        String content = StringUtils.defaultIfBlank(htmlTag, StringUtils.EMPTY);
        content = Jsoup.parse(content).text();

        return content;
    }

}
