package com.jakduk.batch.job;

import com.jakduk.batch.common.Constants;
import com.jakduk.batch.model.db.Gallery;
import com.jakduk.batch.processor.RemoveOldGalleryProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.HashMap;
import java.util.Map;

/**
 * 오래된 사진파일과 DB document를 삭제한다.
 *
 * Created by pyohwan on 16. 10. 6.
 */

@Configuration
public class RemoveOldGalleryConfig {

    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;
    @Autowired private MongoOperations mongoOperations;

    @Bean
    public Job removeOldGalleryJob(@Qualifier("removeOldGalleryStep") Step step1) throws Exception {

        return jobBuilderFactory.get("removeOldGalleryJob")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    @Bean
    public Step removeOldGalleryStep() {
        return stepBuilderFactory.get("removeOldGalleryStep")
                .<Gallery, Gallery>chunk(1000)
                .reader(this.removeOldGalleryReader())
                .processor(this.removeOldGalleryProcessor())
                .writer(new MongoItemWriter<>())
                .build();
    }

    private ItemReader<Gallery> removeOldGalleryReader() {

        String query = String.format("{'status.status':'%s'}",
                Constants.GALLERY_STATUS_TYPE.TEMP);

        MongoItemReader<Gallery> itemReader = new MongoItemReader<>();
        itemReader.setTemplate(mongoOperations);
        itemReader.setTargetType(Gallery.class);
        itemReader.setPageSize(100);
        itemReader.setQuery(query);

        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.DESC);
        itemReader.setSort(sorts);

        return itemReader;
    }

    private ItemProcessor<Gallery, Gallery> removeOldGalleryProcessor() {
        return new RemoveOldGalleryProcessor();
    }

    private MongoItemWriter<Gallery> removeOldGalleryWriter() {
        MongoItemWriter<Gallery> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoOperations);

        return writer;
    }

}
