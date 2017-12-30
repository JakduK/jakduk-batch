package com.jakduk.batch.job;

import com.jakduk.batch.common.Constants;
import com.jakduk.batch.model.db.ArticleComment;
import com.jakduk.batch.processor.ArticleCommentEditBoardProcessor;
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
 * ArticeComment의 article.board와 실제 article의 board를 일치시킴.
 */
@Configuration
public class ArticleCommentEditBoardConfig {

    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;
    @Autowired private MongoOperations mongoOperations;

    @Bean
    public Job articleCommentEditBoardJob(@Qualifier("articleCommentEditBoardStep") Step step) {
        return jobBuilderFactory.get("articleCommentEditBoardJob")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step articleCommentEditBoardStep() {
        return stepBuilderFactory.get("articleCommentEditBoardStep")
                .<ArticleComment, ArticleComment>chunk(1000)
                .reader(articleCommentEditBoardReader())
                .processor(articleCommentEditBoardProcessor())
                .writer(articleCommentEditBoardWriter())
                .build();
    }

    @Bean
    public ItemReader<ArticleComment> articleCommentEditBoardReader() {
        String query = String.format("{'batch':{$nin:['%s']}}",
                Constants.BATCH_TYPE.ARTICLE_COMMENT_EDIT_BOARD_01);

        MongoItemReader<ArticleComment> itemReader = new MongoItemReader<>();
        itemReader.setTemplate(mongoOperations);
        itemReader.setTargetType(ArticleComment.class);
        itemReader.setPageSize(1000);
        itemReader.setQuery(query);
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        itemReader.setSort(sorts);

        return itemReader;
    }

    @Bean
    public ItemProcessor<ArticleComment, ArticleComment> articleCommentEditBoardProcessor() {
        return new ArticleCommentEditBoardProcessor();
    }

    @Bean
    public MongoItemWriter<ArticleComment> articleCommentEditBoardWriter() {
        MongoItemWriter<ArticleComment> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoOperations);

        return writer;
    }
}
