package com.jakduk.batch.job;

import com.jakduk.batch.model.db.ArticleComment;
import com.jakduk.batch.model.db.Mail;
import com.jakduk.batch.model.db.User;
import com.jakduk.batch.repository.MailRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 단체 메일 발송
 */

@Slf4j
@Configuration
public class SendBulkMailConfig {

    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;
    @Autowired private MongoOperations mongoOperations;

    @Autowired private MailRepository mailRepository;

    @Bean
    public Job sendBulkMailJob(
            @Qualifier("getMailByIdStep") Step findOneByIdStep,
            @Qualifier("sendBulkMailStep") Step sendBulkMailStep) {

        return jobBuilderFactory.get("sendBulkMailJob")
                .incrementer(new RunIdIncrementer())
                .start(findOneByIdStep)
                .next(sendBulkMailStep)
                .build();
    }

    @Bean
    public Step getMailByIdStep() {

        return stepBuilderFactory.get("getMailByIdStep")
                .tasklet((contribution, chunkContext) -> {

                    Mail mail = mailRepository.findOneById("5a1c2cff8f9e22033706af4e")
                            .orElseThrow(() -> new NoSuchElementException("해당 Mail을 찾을 수 없습니다."));

                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
                            .put("mail", mail);

                    return RepeatStatus.FINISHED;
                })
                .build();

    }

    @Bean
    public Step sendBulkMailStep() {

        return stepBuilderFactory.get("sendBulkMailStep")
                .<User, User>chunk(1000)
                .reader(sendBulkMailReader())
                .processor(item -> {

                    if (StringUtils.endsWithAny(item.getEmail(), "@jakduk.com", "@tfbnw.net"))
                        return null;

                    log.info("item=" + item);

                    return null;
                })
                .build();

    }

    @Bean
    public ItemReader<User> sendBulkMailReader() {
        MongoItemReader<User> itemReader = new MongoItemReader<>();
        itemReader.setTemplate(mongoOperations);
        itemReader.setTargetType(User.class);
        itemReader.setPageSize(1000);
        itemReader.setQuery("{'email':{$exists:true}}");
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        itemReader.setSort(sorts);

        return itemReader;
    }

}
