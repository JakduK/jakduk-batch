package com.jakduk.batch;

import com.jakduk.batch.configuration.JakdukProperties;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JakdukPropertiesTests {

    @Resource private JakdukProperties.Elasticsearch elasticsearchProperties;
    @Resource private JakdukProperties.Storage storageProperties;

    @Test
    public void elasticsearchProperties() {
        Assert.assertTrue(StringUtils.endsWith(elasticsearchProperties.getIndexBoard(), "_board"));
        Assert.assertTrue(StringUtils.endsWith(elasticsearchProperties.getIndexGallery(), "_gallery"));
        Assert.assertTrue(StringUtils.endsWith(elasticsearchProperties.getIndexSearchWord(), "_search_word"));
        Assert.assertNotNull(elasticsearchProperties.getBulkActions());
        Assert.assertNotNull(elasticsearchProperties.getBulkConcurrentRequests());
        Assert.assertNotNull(elasticsearchProperties.getBulkFlushIntervalSeconds());
        Assert.assertNotNull(elasticsearchProperties.getBulkSizeMb());
    }

    @Test
    public void storateProperties() {
        Assert.assertNotNull(storageProperties.getImagePath());
        Assert.assertNotNull(storageProperties.getThumbnailPath());
        Assert.assertNotNull(storageProperties.getUserPictureLargePath());
        Assert.assertNotNull(storageProperties.getUserPictureSmallPath());
    }
}
