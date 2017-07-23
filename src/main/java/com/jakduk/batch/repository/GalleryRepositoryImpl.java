package com.jakduk.batch.repository;

import com.jakduk.batch.common.JakdukConst;
import com.jakduk.batch.model.db.Gallery;
import com.jakduk.batch.model.elasticsearch.EsGallery;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Created by pyohwan on 16. 11. 30.
 */

@Repository
public class GalleryRepositoryImpl implements GalleryRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<EsGallery> findGalleriesGreaterThanId(ObjectId objectId, Integer limit) {
        AggregationOperation match1 = Aggregation.match(Criteria.where("_id").gt(objectId));
        AggregationOperation sort = Aggregation.sort(Sort.Direction.ASC, "_id");
        AggregationOperation limit1 = Aggregation.limit(limit);

        Aggregation aggregation;

        if (! ObjectUtils.isEmpty(objectId)) {
            aggregation = Aggregation.newAggregation(match1, sort, limit1);
        } else {
            aggregation = Aggregation.newAggregation(sort, limit1);
        }

        AggregationResults<EsGallery> results = mongoTemplate.aggregate(aggregation, JakdukConst.COLLECTION_GALLERY, EsGallery.class);

        return results.getMappedResults();
    }

    /**
     * ItemID와 FromType에 해당하는 Gallery 목록을 가져온다.
     */
    @Override
    public List<Gallery> findByItemIdAndFromType(ObjectId itemId, JakdukConst.GALLERY_FROM_TYPE fromType, Integer limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status.status").is(JakdukConst.GALLERY_STATUS_TYPE.ENABLE.name()));
        query.addCriteria(Criteria.where("linkedItems._id").is(itemId));
        query.addCriteria(Criteria.where("linkedItems.from").is(fromType));
        query.limit(limit);

        query.with(new Sort(Sort.Direction.DESC, "_id"));

        return mongoTemplate.find(query, Gallery.class);
    }

}
