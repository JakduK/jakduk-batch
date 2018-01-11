package com.jakduk.batch.job;

import com.jakduk.batch.common.Constants;
import com.jakduk.batch.model.db.Article;
import com.jakduk.batch.processor.BoardFreeAddShortContentProcessor;
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
 * 본문 미리보기 용으로, HTML이 제거된 100자 정도의 본문 요약 필드가 필요하다
 *
 * Created by pyohwanjang on 2017. 3. 2..
 */

@Configuration
public class BoardFreeAddShortContentConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MongoOperations mongoOperations;

    @Bean
    public Job boardFreeAddShortContentJob(@Qualifier("boardFreeAddShortContentStep") Step step) {

        return jobBuilderFactory.get("boardFreeAddShortContentJob")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step boardFreeAddShortContentStep() {
        return stepBuilderFactory.get("boardFreeAddShortContentStep")
                .<Article, Article>chunk(1000)
                .reader(boardFreeAddShortContentReader())
                .processor(boardFreeAddShortContentProcessor())
                .writer(boardFreeAddShortContentWriter())
                .build();
    }

    @Bean
    public ItemReader<Article> boardFreeAddShortContentReader() {

        String query = String.format("{'batch':{$nin:['%s']}}",
                Constants.BATCH_TYPE.BOARD_FREE_ADD_SHORT_CONTENT_01);

        MongoItemReader<Article> itemReader = new MongoItemReader<>();
        itemReader.setTemplate(mongoOperations);
        itemReader.setTargetType(Article.class);
        itemReader.setPageSize(500);
        itemReader.setQuery(query);
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        itemReader.setSort(sorts);

        return itemReader;
    }

    @Bean
    public ItemProcessor<Article, Article> boardFreeAddShortContentProcessor() {
        return new BoardFreeAddShortContentProcessor();
    }

    @Bean
    public MongoItemWriter<Article> boardFreeAddShortContentWriter() {
        MongoItemWriter<Article> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoOperations);

        return writer;
    }

}
