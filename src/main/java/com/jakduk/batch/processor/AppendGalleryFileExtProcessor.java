package com.jakduk.batch.processor;

import com.jakduk.batch.common.Constants;
import com.jakduk.batch.configuration.JakdukProperties;
import com.jakduk.batch.model.db.Gallery;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;

import javax.annotation.Resource;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by pyohwan on 16. 10. 5.
 */
public class AppendGalleryFileExtProcessor implements ItemProcessor<Gallery, Gallery> {

    @Resource private JakdukProperties.Storage storageProperties;

    @Override
    public Gallery process(Gallery item) throws Exception {

        ObjectId objId = new ObjectId(item.getId());
        Instant instant = Instant.ofEpochMilli(objId.getDate().getTime());
        LocalDateTime timePoint = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        String formatName = StringUtils.split(item.getContentType(), "/")[1];

        Path moveFromImage = Paths.get(storageProperties.getImagePath(), String.valueOf(timePoint.getYear()), String.valueOf(timePoint.getMonthValue()),
                String.valueOf(timePoint.getDayOfMonth()), item.getId());

        Path moveFromThumb = Paths.get(storageProperties.getThumbnailPath(), String.valueOf(timePoint.getYear()), String.valueOf(timePoint.getMonthValue()),
                String.valueOf(timePoint.getDayOfMonth()), item.getId());

        if (Files.exists(moveFromImage, LinkOption.NOFOLLOW_LINKS) && Files.exists(moveFromThumb, LinkOption.NOFOLLOW_LINKS)) {
            Files.move(moveFromImage, moveFromImage.resolveSibling(item.getId() + "." + formatName), StandardCopyOption.REPLACE_EXISTING);
            Files.move(moveFromThumb, moveFromThumb.resolveSibling(item.getId() + "." + formatName), StandardCopyOption.REPLACE_EXISTING);

            List<Constants.BATCH_TYPE> batchList = Optional.ofNullable(item.getBatch())
                    .orElseGet(ArrayList::new);

            if (batchList.stream().noneMatch(batch -> batch.equals(Constants.BATCH_TYPE.APPEND_GALLERY_FILE_EXT_01))) {
                batchList.add(Constants.BATCH_TYPE.APPEND_GALLERY_FILE_EXT_01);
                item.setBatch(batchList);
            }

            System.out.println(item);
        }

        return item;
    }
}
