package com.jakduk.batch.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Jang,Pyohwan on 2017. 6. 12..
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jakduk")
public class JakdukProperties {

    private Elasticsearch elasticsearch = new Elasticsearch();
    private Storage storage = new Storage();

    @Getter
    @Setter
    @Configuration
    public class Elasticsearch {
        private Boolean enable;
        private String indexBoard;
        private String indexGallery;
        private String indexSearchWord;
        private Integer bulkActions;
        private Integer bulkConcurrentRequests;
        private Integer bulkFlushIntervalSeconds;
        private Integer bulkSizeMb;
    }

    @Getter
    @Setter
    @Configuration
    public class Storage {
        private String imagePath;
        private String thumbnailPath;
        private String userPictureLargePath;
        private String userPictureSmallPath;
    }

}
