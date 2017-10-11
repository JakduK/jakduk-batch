package com.jakduk.batch.repository;

import com.jakduk.batch.model.db.Article;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by pyohwan on 16. 10. 9.
 */
public interface ArticleRepositoryCustom {

    /**
     * 기준 Article ID 이상의 Article 목록을 가져온다.
     */
    List<Article> findPostsGreaterThanId(ObjectId objectId, Integer limit);

}