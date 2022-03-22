package com.jakduk.batch.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jakduk.batch.common.Constants;
import com.jakduk.batch.common.JakdukUtils;
import com.jakduk.batch.common.ObjectMapperUtils;
import com.jakduk.batch.configuration.JakdukProperties;
import com.jakduk.batch.model.db.Article;
import com.jakduk.batch.model.db.ArticleComment;
import com.jakduk.batch.model.db.Gallery;
import com.jakduk.batch.model.elasticsearch.EsArticle;
import com.jakduk.batch.model.elasticsearch.EsComment;
import com.jakduk.batch.model.elasticsearch.EsGallery;
import com.jakduk.batch.repository.ArticleCommentRepository;
import com.jakduk.batch.repository.ArticleRepository;
import com.jakduk.batch.repository.GalleryRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by pyohwanjang on 2017. 4. 7..
 */

@Slf4j
@Service
public class SearchService {

	@Resource
	private JakdukProperties.Elasticsearch elasticsearchProperties;

	@Autowired
	private RestHighLevelClient highLevelClient;
	@Autowired
	private ArticleRepository articleRepository;
	@Autowired
	private GalleryRepository galleryRepository;
	@Autowired
	private ArticleCommentRepository articleCommentRepository;

	public void createIndexBoard() throws IOException {

		String index = elasticsearchProperties.getIndexBoard();

		CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
		createIndexRequest.settings(this.getIndexSettings());
		createIndexRequest.mapping(this.getBoardMappings());

		CreateIndexResponse createIndexResponse = highLevelClient.indices()
			.create(createIndexRequest, RequestOptions.DEFAULT);

		if (createIndexResponse.isAcknowledged()) {
			log.debug("Index " + index + " created");
		} else {
			throw new RuntimeException("Index " + index + " not created");
		}
	}

	public void createIndexGallery() throws IOException {

		String index = elasticsearchProperties.getIndexGallery();

		CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
		createIndexRequest.settings(this.getIndexSettings());
		createIndexRequest.mapping(this.getGalleryMappings());

		CreateIndexResponse createIndexResponse = highLevelClient.indices()
			.create(createIndexRequest, RequestOptions.DEFAULT);

		if (createIndexResponse.isAcknowledged()) {
			log.debug("Index " + index + " created");
		} else {
			throw new RuntimeException("Index " + index + " not created");
		}
	}

	public void createIndexSearchWord() throws IOException {

		String index = elasticsearchProperties.getIndexSearchWord();

		CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
		createIndexRequest.settings(this.getIndexSettings());
		createIndexRequest.mapping(this.getSearchWordMappings());

		CreateIndexResponse createIndexResponse = highLevelClient.indices()
			.create(createIndexRequest, RequestOptions.DEFAULT);

		if (createIndexResponse.isAcknowledged()) {
			log.debug("Index " + index + " created");
		} else {
			throw new RuntimeException("Index " + index + " not created");
		}
	}

	public void processBulkInsertArticle() throws InterruptedException {

		BulkProcessor bulkProcessor = getBulkProcessor();

		boolean hasPost = true;
		ObjectId lastPostId = null;

		do {
			List<Article> articles = articleRepository.findPostsGreaterThanId(lastPostId, Constants.ES_BULK_LIMIT);

			List<EsArticle> esArticles = articles.stream()
				.filter(Objects::nonNull)
				.map(post -> {
					List<String> galleryIds = null;

					if (post.getLinkedGallery()) {
						List<Gallery> galleries = galleryRepository.findByItemIdAndFromType(
							new ObjectId(post.getId()), Constants.GALLERY_FROM_TYPE.ARTICLE.name(), 1);

						galleryIds = galleries.stream()
							.filter(Objects::nonNull)
							.map(Gallery::getId)
							.collect(Collectors.toList());
					}

					return EsArticle.builder()
						.id(post.getId())
						.seq(post.getSeq())
						.board(post.getBoard())
						.writer(post.getWriter())
						.subject(JakdukUtils.stripHtmlTag(post.getSubject()))
						.content(JakdukUtils.stripHtmlTag(post.getContent()))
						.category(StringUtils.defaultIfBlank(post.getCategory(), null))
						.galleries(galleryIds)
						.boardJoinField(Constants.ES_TYPE_ARTICLE)
						.build();
				})
				.collect(Collectors.toList());

			if (esArticles.isEmpty()) {
				hasPost = false;
			} else {
				EsArticle lastPost = esArticles.get(esArticles.size() - 1);
				lastPostId = new ObjectId(lastPost.getId());
			}

			esArticles.forEach(post -> {
				try {
					IndexRequest indexRequest = new IndexRequest(elasticsearchProperties.getIndexBoard());
					indexRequest.id(post.getId());
					indexRequest.source(ObjectMapperUtils.writeValueAsString(post), XContentType.JSON);
					bulkProcessor.add(indexRequest);
				} catch (JsonProcessingException e) {
					log.error(e.getLocalizedMessage());
				}

			});

		} while (hasPost);

		bulkProcessor.awaitClose(Constants.ES_AWAIT_CLOSE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
	}

	public void processBulkInsertArticleComment() throws InterruptedException {

		BulkProcessor bulkProcessor = getBulkProcessor();

		Boolean hasComment = true;
		ObjectId lastCommentId = null;

		do {
			List<ArticleComment> comments = articleCommentRepository.findCommentsGreaterThanId(lastCommentId,
				Constants.ES_BULK_LIMIT);

			List<EsComment> esComments = comments.stream()
				.filter(Objects::nonNull)
				.map(comment -> {
					List<String> galleryIds = null;

					if (comment.getLinkedGallery()) {
						List<Gallery> galleries = galleryRepository.findByItemIdAndFromType(
							new ObjectId(comment.getId()), Constants.GALLERY_FROM_TYPE.ARTICLE_COMMENT.name(), 1);

						galleryIds = galleries.stream()
							.filter(Objects::nonNull)
							.map(Gallery::getId)
							.collect(Collectors.toList());
					}

					return EsComment.builder()
						.id(comment.getId())
						.article(comment.getArticle())
						.writer(comment.getWriter())
						.content(JakdukUtils.stripHtmlTag(comment.getContent()))
						.galleries(galleryIds)
						.boardJoinField(
							new HashMap<String, Object>() {{
								put("name", Constants.ES_TYPE_COMMENT);
								put("parent", comment.getArticle().getId());
							}}
						)
						.build();
				})
				.collect(Collectors.toList());

			if (esComments.isEmpty()) {
				hasComment = false;
			} else {
				EsComment lastComment = esComments.get(esComments.size() - 1);
				lastCommentId = new ObjectId(lastComment.getId());
			}

			esComments.forEach(comment -> {
				try {
					IndexRequest indexRequest = new IndexRequest(elasticsearchProperties.getIndexBoard());
					indexRequest.id(comment.getId());
					indexRequest.source(ObjectMapperUtils.writeValueAsString(comment), XContentType.JSON);
					bulkProcessor.add(indexRequest);
				} catch (JsonProcessingException e) {
					log.error(e.getLocalizedMessage());
				}

			});

		} while (hasComment);

		bulkProcessor.awaitClose(Constants.ES_AWAIT_CLOSE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
	}

	public void processBulkInsertGallery() throws InterruptedException {

		BulkProcessor bulkProcessor = getBulkProcessor();

		Boolean hasGallery = true;
		ObjectId lastGalleryId = null;

		do {
			List<EsGallery> comments = galleryRepository.findGalleriesGreaterThanId(lastGalleryId,
				Constants.ES_BULK_LIMIT);

			if (comments.isEmpty()) {
				hasGallery = false;
			} else {
				EsGallery lastGallery = comments.get(comments.size() - 1);
				lastGalleryId = new ObjectId(lastGallery.getId());
			}

			comments.forEach(comment -> {
				try {
					IndexRequest indexRequest = new IndexRequest(elasticsearchProperties.getIndexGallery(),
						Constants.ES_TYPE_GALLERY, comment.getId());
					indexRequest.source(ObjectMapperUtils.writeValueAsString(comment), XContentType.JSON);
					bulkProcessor.add(indexRequest);

				} catch (JsonProcessingException e) {
					log.error(e.getLocalizedMessage());
				}

			});

		} while (hasGallery);

		bulkProcessor.awaitClose(Constants.ES_AWAIT_CLOSE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
	}

	public boolean existsIndex(String index) throws IOException {
		return highLevelClient.indices()
			.exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
	}

	public void deleteIndex(String index) throws IOException {
		AcknowledgedResponse response = highLevelClient.indices()
			.delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);

		if (response.isAcknowledged()) {
			log.debug("Index {} deleted." + index);
		} else {
			throw new RuntimeException("Index " + index + " not deleted");
		}
	}

	private BulkProcessor getBulkProcessor() {
		BulkProcessor.Listener bulkProcessorListener = new BulkProcessor.Listener() {
			@Override
			public void beforeBulk(long l, BulkRequest bulkRequest) {
			}

			@Override
			public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
				log.debug("bulk took: {}", bulkResponse.getTook());

				if (bulkResponse.hasFailures())
					log.error(bulkResponse.buildFailureMessage());
			}

			@Override
			public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
				log.error(throwable.getLocalizedMessage());
			}
		};

		return BulkProcessor.builder(
				(bulkRequest, bulkResponseActionListener) -> highLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT,
					bulkResponseActionListener),
				bulkProcessorListener)
			.setBulkActions(elasticsearchProperties.getBulkActions())
			.setBulkSize(new ByteSizeValue(elasticsearchProperties.getBulkSizeMb(), ByteSizeUnit.MB))
			.setFlushInterval(TimeValue.timeValueSeconds(elasticsearchProperties.getBulkFlushIntervalSeconds()))
			.setConcurrentRequests(elasticsearchProperties.getBulkConcurrentRequests())
			.build();
	}

	private Settings getIndexSettings() {
		String[] userWords = new String[] {
			"k리그",
			"k리그1",
			"k리그2",
			"k3리그",
			"k4리그",
			"성남fc",
			"수원fc",
			"인천utd",
			"인천유나이티드",
			"강원fc",
			"fc서울",
			"fc안양",
			"부천fc",
			"대구fc",
			"광주fc",
			"경남fc",
			"제주utd",
			"제주유나이티드",
			"서울e"
		};

		// SEE: https://www.elastic.co/guide/en/elasticsearch/plugins/7.17/analysis-nori-tokenizer.html
		return Settings.builder()
			.put("index.analysis.tokenizer.korean_nori_tokenizer.type", "nori_tokenizer")
			.put("index.analysis.tokenizer.korean_nori_tokenizer.decompound_mode", "none")
			.putList("index.analysis.tokenizer.korean_nori_tokenizer.user_dictionary_rules", userWords)
			.put("index.analysis.analyzer.korean.type", "custom")
			.put("index.analysis.analyzer.korean.tokenizer", "korean_nori_tokenizer")
			.build();
	}

	private Map getBoardMappings() {
		return new HashMap<String, Object>() {{
			put("properties", new HashMap<String, Object>() {{
				put("id", new HashMap<String, String>() {{
					put("type", "keyword");
				}});
				put("seq", new HashMap<String, Object>() {{
					put("type", "integer");
					put("index", false);
				}});
				put("board", new HashMap<String, Object>() {{
					put("type", "keyword");
					put("index", false);
				}});
				put("category", new HashMap<String, Object>() {{
					put("type", "keyword");
					put("index", false);
				}});
				put("subject", new HashMap<String, String>() {{
					put("type", "text");
					put("analyzer", "korean");
				}});
				put("content", new HashMap<String, String>() {{
					put("type", "text");
					put("analyzer", "korean");
				}});
				put("galleries", new HashMap<String, Object>() {{
					put("type", "keyword");
					put("index", false);
				}});
				put("writer", new HashMap<String, Object>() {{
					put("properties", new HashMap<String, Object>() {{
						put("providerId", new HashMap<String, Object>() {{
							put("type", "keyword");
							put("index", false);
						}});
						put("userId", new HashMap<String, Object>() {{
							put("type", "keyword");
							put("index", false);
						}});
						put("username", new HashMap<String, Object>() {{
							put("type", "keyword");
							put("index", false);
						}});
					}});
				}});
				put("registerDate", new HashMap<String, String>() {{
					put("type", "date");
				}});
				// for Comment Index
				put("article", new HashMap<String, Object>() {{
					put("properties", new HashMap<String, Object>() {{
						put("id", new HashMap<String, Object>() {{
							put("type", "keyword");
							put("index", false);
						}});
						put("seq", new HashMap<String, Object>() {{
							put("type", "integer");
							put("index", false);
						}});
						put("board", new HashMap<String, Object>() {{
							put("type", "keyword");
							put("index", false);
						}});
					}});
				}});
				// for Join field type
				put("boardJoinField", new HashMap<String, Object>() {{
					put("type", "join");
					put("relations", new HashMap<String, String>() {{
						put(Constants.ES_TYPE_ARTICLE, Constants.ES_TYPE_COMMENT);
					}});
				}});
			}});
		}};
	}

	private Map<String, Object> getGalleryMappings() {
		ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();

		ObjectNode idNode = objectMapper.createObjectNode();
		idNode.put("type", "string");

		ObjectNode nameNode = objectMapper.createObjectNode();
		nameNode.put("type", "string");
		nameNode.put("analyzer", "korean");

		// writer
		ObjectNode writerProviderIdNode = objectMapper.createObjectNode();
		writerProviderIdNode.put("type", "string");
		writerProviderIdNode.put("index", "no");

		ObjectNode writerUserIdNode = objectMapper.createObjectNode();
		writerUserIdNode.put("type", "string");
		writerUserIdNode.put("index", "no");

		ObjectNode writerUsernameNode = objectMapper.createObjectNode();
		writerUsernameNode.put("type", "string");
		writerUsernameNode.put("index", "no");

		ObjectNode writerPropertiesNode = objectMapper.createObjectNode();
		writerPropertiesNode.set("providerId", writerProviderIdNode);
		writerPropertiesNode.set("userId", writerUserIdNode);
		writerPropertiesNode.set("username", writerUsernameNode);

		ObjectNode writerNode = objectMapper.createObjectNode();
		writerNode.set("properties", writerPropertiesNode);

		// properties
		ObjectNode propertiesNode = objectMapper.createObjectNode();
		propertiesNode.set("id", idNode);
		propertiesNode.set("name", nameNode);
		propertiesNode.set("writer", writerNode);

		ObjectNode mappings = objectMapper.createObjectNode();
		mappings.set("properties", propertiesNode);

		return objectMapper.convertValue(mappings, Map.class);
	}

	private Map<String, Object> getSearchWordMappings() {
		ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();

		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

		ObjectNode propertiesNode = jsonNodeFactory.objectNode();

		propertiesNode.set("id",
			jsonNodeFactory.objectNode()
				.put("type", "string"));

		propertiesNode.set("word",
			jsonNodeFactory.objectNode()
				.put("type", "string")
				.put("index", "not_analyzed")
		);

		ObjectNode writerNode = jsonNodeFactory.objectNode();
		writerNode.set("providerId", jsonNodeFactory.objectNode().put("type", "string").put("index", "no"));
		writerNode.set("userId", jsonNodeFactory.objectNode().put("type", "string").put("index", "no"));
		writerNode.set("username", jsonNodeFactory.objectNode().put("type", "string").put("index", "no"));
		propertiesNode.set("writer", jsonNodeFactory.objectNode().set("properties", writerNode));

		propertiesNode.set("registerDate",
			jsonNodeFactory.objectNode()
				.put("type", "date")
		);

		ObjectNode mappings = jsonNodeFactory.objectNode();
		mappings.set("properties", propertiesNode);

		return objectMapper.convertValue(mappings, Map.class);
	}
}
