package com.jakduk.batch.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Jang,Pyohwan on 2017. 6. 12..
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties("jakduk")
public class JakdukProperties {

    private Elasticsearch elasticsearch = new Elasticsearch();
    private Storage storage = new Storage();

    @Getter
    @Setter
    @Configuration
    @ConfigurationProperties("jakduk.elasticsearch")
    public class Elasticsearch {
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
    @ConfigurationProperties("jakduk.storage")
    public class Storage {
        private String imagePath;
        private String thumbnailPath;
        private String userPictureLargePath;
        private String userPictureSmallPath;
    }

    @Getter
    @Setter
    @Configuration
    @ConfigurationProperties("jakduk.api-server")
    public class ApiServer {
        private String serverUrl;
        private String sendBulkMail;
    }

}
