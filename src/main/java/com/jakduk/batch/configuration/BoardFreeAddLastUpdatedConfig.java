package com.jakduk.batch.configuration;

import com.jakduk.batch.common.JakdukConst;
import com.jakduk.batch.model.db.Article;
import com.jakduk.batch.processor.BoardFreeAddLastUpdatedProcessor;
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
 * BoardFree에 lastUpdated 필드를 추가한다.
 *
 * Created by pyohwanjang on 2017. 3. 12..
 */

@Configuration
public class BoardFreeAddLastUpdatedConfig {

    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;
    @Autowired private MongoOperations mongoOperations;

    @Bean
    public Job boardFreeAddLastUpdatedJob(@Qualifier("boardFreeAddLastUpdatedStep") Step step) {

        return jobBuilderFactory.get("boardFreeAddLastUpdatedJob")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step boardFreeAddLastUpdatedStep() {
        return stepBuilderFactory.get("boardFreeAddLastUpdatedStep")
                .<Article, Article>chunk(1000)
                .reader(boardFreeAddLastUpdatedReader())
                .processor(boardFreeAddLastUpdatedProcessor())
                .writer(boardFreeAddLastUpdatedWriter())
                .build();
    }

    @Bean
    public ItemReader<Article> boardFreeAddLastUpdatedReader() {

        String query = String.format("{'batch':{$nin:['%s']}}",
                JakdukConst.BATCH_TYPE.BOARD_FREE_ADD_LAST_UPDATED_01);

        MongoItemReader<Article> itemReader = new MongoItemReader<>();
        itemReader.setTemplate(mongoOperations);
        itemReader.setTargetType(Article.class);
        itemReader.setPageSize(1000);
        itemReader.setQuery(query);
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        itemReader.setSort(sorts);

        return itemReader;
    }

    @Bean
    public ItemProcessor<Article, Article> boardFreeAddLastUpdatedProcessor() {
        return new BoardFreeAddLastUpdatedProcessor();
    }

    @Bean
    public MongoItemWriter<Article> boardFreeAddLastUpdatedWriter() {
        MongoItemWriter<Article> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoOperations);

        return writer;
    }

}
