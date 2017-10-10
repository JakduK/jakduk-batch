package com.jakduk.batch.processor;


import com.jakduk.batch.common.JakdukConst;
import com.jakduk.batch.common.JakdukUtils;
import com.jakduk.batch.model.db.Article;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by pyohwanjang on 2017. 3. 2..
 */
public class BoardFreeAddShortContentProcessor implements ItemProcessor<Article, Article> {

    @Override
    public Article process(Article item) throws Exception {

        String stripHtmlContent = StringUtils.defaultIfBlank(JakdukUtils.stripHtmlTag(item.getContent()), StringUtils.EMPTY);
        stripHtmlContent = StringUtils.truncate(stripHtmlContent, JakdukConst.ARTICLE_SHORT_CONTENT_LENGTH);

        if (StringUtils.isNotBlank(stripHtmlContent)) {
            item.setShortContent(stripHtmlContent);

            List<JakdukConst.BATCH_TYPE> batchList = Optional.ofNullable(item.getBatch())
                    .orElseGet(ArrayList::new);

            if (batchList.stream().noneMatch(batch -> batch.equals(JakdukConst.BATCH_TYPE.BOARD_FREE_ADD_SHORT_CONTENT_01))) {
                batchList.add(JakdukConst.BATCH_TYPE.BOARD_FREE_ADD_SHORT_CONTENT_01);
                item.setBatch(batchList);
            }
        }

        return item;
    }
}
