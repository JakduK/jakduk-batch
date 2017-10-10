package com.jakduk.batch.repository;

import com.jakduk.batch.common.JakdukConst;
import com.jakduk.batch.model.db.Gallery;
import com.jakduk.batch.model.elasticsearch.EsGallery;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by pyohwan on 16. 11. 30.
 */
public interface GalleryRepositoryCustom {

    // 기준 Gallery ID 이상의 Gallery 목록을 가져온다.
    List<EsGallery> findGalleriesGreaterThanId(ObjectId objectId, Integer limit);

    /**
     * ItemID와 FromType에 해당하는 Gallery 목록을 가져온다.
     */
    List<Gallery> findByItemIdAndFromType(ObjectId itemId, String fromType, Integer limit);

}
