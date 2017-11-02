package com.jakduk.batch.processor;

import com.jakduk.batch.common.Constants;
import com.jakduk.batch.model.db.Article;
import com.jakduk.batch.model.db.ArticleComment;
import com.jakduk.batch.model.embedded.ArticleItem;
import com.jakduk.batch.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ArticleCommentEditBoardProcessor implements ItemProcessor<ArticleComment, ArticleComment> {

    @Autowired private ArticleRepository articleRepository;

    @Override
    public ArticleComment process(ArticleComment item) throws Exception {

        ArticleItem articleItem = item.getArticle();
        Optional<Article> optArticle = articleRepository.findOneById(articleItem.getId());

        if (optArticle.isPresent()) {

            Article article = optArticle.get();

            if (! article.getBoard().equals(articleItem.getBoard())) {
                articleItem.setBoard(article.getBoard());

                List<Constants.BATCH_TYPE> batchList = Optional.ofNullable(item.getBatch())
                        .orElseGet(ArrayList::new);

                if (batchList.stream().noneMatch(batch -> batch.equals(Constants.BATCH_TYPE.BOARD_FREE_COMMENT_ADD_HISTORY_01))) {
                    batchList.add(Constants.BATCH_TYPE.BOARD_FREE_COMMENT_ADD_HISTORY_01);
                    item.setBatch(batchList);
                }

                log.info("Item id:{}, articleItem:{}", item.getId(), articleItem);

                return item;
            }
        }

        return null;

    }
}
