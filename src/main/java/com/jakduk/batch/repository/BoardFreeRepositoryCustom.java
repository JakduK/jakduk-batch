package com.jakduk.batch.repository;

import com.jakduk.batch.model.db.BoardFree;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by pyohwan on 16. 10. 9.
 */
public interface BoardFreeRepositoryCustom {

    /**
     * 기준 BoardFree ID 이상의 BoardFree 목록을 가져온다.
     */
    List<BoardFree> findPostsGreaterThanId(ObjectId objectId, Integer limit);

}