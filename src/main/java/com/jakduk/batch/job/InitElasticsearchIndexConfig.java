package com.jakduk.batch.job;

import javax.annotation.Resource;

import org.elasticsearch.index.IndexNotFoundException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jakduk.batch.configuration.JakdukProperties;
import com.jakduk.batch.service.SearchService;

import lombok.extern.slf4j.Slf4j;

/**
 * 엘라스틱서치의 인덱스, 타입, 도큐먼트를 지우고 새로 만든다.
 * DB에 있는 게시물, 댓글, 사진첩의 데이터를 가져와 벌크 방식으로 입력한다.
 *
 * Created by pyohwan on 16. 9. 18.
 */

@Slf4j
@Configuration
public class InitElasticsearchIndexConfig {

	@Resource
	private JakdukProperties.Elasticsearch elasticsearchProperties;
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private SearchService searchService;

	@Bean
	public Job initElasticsearchIndexJob(
		@Qualifier("deleteIndexStep") Step deleteIndexStep,
		@Qualifier("initSearchIndexStep") Step initSearchIndexStep,
		@Qualifier("initSearchDocumentsStep") Step initSearchDocumentsStep) {

		return jobBuilderFactory.get("initElasticsearchIndexJob")
			.incrementer(new RunIdIncrementer())
			.start(deleteIndexStep)
			.next(initSearchIndexStep)
			.next(initSearchDocumentsStep)
			.build();
	}

	@Bean
	public Step deleteIndexStep() {
		return stepBuilderFactory.get("deleteIndexStep")
			.tasklet((contribution, chunkContext) -> {

				try {
					String indexBoard = elasticsearchProperties.getIndexBoard();
					if (searchService.existsIndex(indexBoard)) {
						searchService.deleteIndex(indexBoard);
					}

					String indexGallery = elasticsearchProperties.getIndexGallery();
					if (searchService.existsIndex(indexGallery)) {
						searchService.deleteIndex(indexGallery);
					}

				} catch (IndexNotFoundException e) {
					log.warn(e.getDetailedMessage());
				}

				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Step initSearchIndexStep() {
		return stepBuilderFactory.get("initSearchIndexStep")
			.tasklet((contribution, chunkContext) -> {

				searchService.createIndexBoard();
				searchService.createIndexGallery();

				// search-word 는 인덱스를 새로 만들지 않음. 따라서 기존할 경우, skip 함
				String indexSearchWord = elasticsearchProperties.getIndexSearchWord();
				if (! searchService.existsIndex(indexSearchWord)) {
					searchService.createIndexSearchWord();
				}

				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Step initSearchDocumentsStep() {
		return stepBuilderFactory.get("initSearchDocumentsStep")
			.tasklet((contribution, chunkContext) -> {

				searchService.processBulkInsertArticle();
				searchService.processBulkInsertArticleComment();
				searchService.processBulkInsertGallery();

				return RepeatStatus.FINISHED;
			})
			.build();
	}

}
