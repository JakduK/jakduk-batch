package com.jakduk.batch.processor;

import com.jakduk.batch.common.Constants;
import com.jakduk.batch.model.db.Gallery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by pyohwanjang on 2017. 4. 21..
 */
public class GalleryCheckNameProcessor implements ItemProcessor<Gallery, Gallery> {

    @Override
    public Gallery process(Gallery item) throws Exception {

        String name = item.getName();
        String fileName = item.getFileName();

        if (StringUtils.isNoneBlank(name, fileName) && name.equals(fileName)) {
            item.setName(null);

            List<Constants.BATCH_TYPE> batchList = Optional.ofNullable(item.getBatch())
                    .orElseGet(ArrayList::new);

            if (batchList.stream().noneMatch(batch -> batch.equals(Constants.BATCH_TYPE.GALLERY_CHECK_NAME_01))) {
                batchList.add(Constants.BATCH_TYPE.GALLERY_CHECK_NAME_01);
                item.setBatch(batchList);
            }
        }

        return item;
    }

}
