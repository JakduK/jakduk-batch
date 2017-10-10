package com.jakduk.batch.repository;

import com.jakduk.batch.common.Constants;
import com.jakduk.batch.model.db.Article;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Created by pyohwan on 16. 10. 9.
 */

@Repository
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 기준 Article ID 이상의 Article 목록을 가져온다.
     */
    @Override
    public List<Article> findPostsGreaterThanId(ObjectId objectId, Integer limit) {
        AggregationOperation match1 = Aggregation.match(Criteria.where("status.delete").ne(true));
        AggregationOperation match2 = Aggregation.match(Criteria.where("_id").gt(objectId));
        AggregationOperation sort = Aggregation.sort(Sort.Direction.ASC, "_id");
        AggregationOperation limit1 = Aggregation.limit(limit);

        Aggregation aggregation;

        if (! ObjectUtils.isEmpty(objectId)) {
            aggregation = Aggregation.newAggregation(match1, match2, sort, limit1);
        } else {
            aggregation = Aggregation.newAggregation(match1, sort, limit1);
        }

        AggregationResults<Article> results = mongoTemplate.aggregate(aggregation, Constants.COLLECTION_ARTICLE, Article.class);

        return results.getMappedResults();

    }

}
