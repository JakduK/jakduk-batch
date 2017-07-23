package com.jakduk.batch.processor;

import com.jakduk.batch.configuration.JakdukProperties;
import com.jakduk.batch.model.db.Gallery;
import com.jakduk.batch.repository.GalleryRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by pyohwan on 16. 10. 6.
 */
public class RemoveOldGalleryProcessor implements ItemProcessor<Gallery, Gallery> {

    @Resource private JakdukProperties.Storage storageProperties;

    @Autowired private GalleryRepository galleryRepository;

    @Override
    public Gallery process(Gallery item) {

        ObjectId objId = new ObjectId(item.getId());
        Instant instant = Instant.ofEpochMilli(objId.getDate().getTime());
        LocalDateTime timePoint = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        String formatName = StringUtils.split(item.getContentType(), "/")[1];

        Path imagePath = Paths.get(storageProperties.getImagePath(), String.valueOf(timePoint.getYear()), String.valueOf(timePoint.getMonthValue()),
                String.valueOf(timePoint.getDayOfMonth()), item.getId() + "." + formatName);

        Path thumbPath = Paths.get(storageProperties.getThumbnailPath(), String.valueOf(timePoint.getYear()), String.valueOf(timePoint.getMonthValue()),
                String.valueOf(timePoint.getDayOfMonth()), item.getId() + "." + formatName);

        if (Files.exists(imagePath, LinkOption.NOFOLLOW_LINKS) && Files.exists(thumbPath, LinkOption.NOFOLLOW_LINKS)) {
            try {
                deleteGalleryFile(imagePath);
                deleteGalleryFile(thumbPath);
                System.out.println("path=" + imagePath + ", gallery id=" + item.getId());

                galleryRepository.delete(item);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Not exist path=" + imagePath);
        }

        return item;
    }

    private void deleteGalleryFile(Path path) throws IOException {
        Files.deleteIfExists(path);
        Path imagePathOfDay = path.getParent();
        DirectoryStream<Path> imagePathOfDayDs = Files.newDirectoryStream(imagePathOfDay);

        if (! imagePathOfDayDs.iterator().hasNext()) {
            Files.deleteIfExists(imagePathOfDay);

            Path imagePathOfMonth = imagePathOfDay.getParent();
            DirectoryStream<Path> imagePathOfMonthDs = Files.newDirectoryStream(imagePathOfMonth);

            if (! imagePathOfMonthDs.iterator().hasNext()) {
                Files.deleteIfExists(imagePathOfMonth);

                Path imagePathOfYear = imagePathOfMonth.getParent();
                DirectoryStream<Path> imagePathOfYearDs = Files.newDirectoryStream(imagePathOfYear);

                if (! imagePathOfYearDs.iterator().hasNext())
                    Files.deleteIfExists(imagePathOfYear);
            }
        }
    }
}
