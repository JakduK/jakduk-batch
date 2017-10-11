package com.jakduk.batch.repository;

import com.jakduk.batch.model.db.ArticleComment;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by pyohwan on 16. 11. 30.
 */
public interface ArticleCommentRepositoryCustom {

    /**
     * 기준 ArticleComment ID 이상의 ArticleComment 목록을 가져온다.
     */
    List<ArticleComment> findCommentsGreaterThanId(ObjectId objectId, Integer limit);

}
