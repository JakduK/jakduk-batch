package com.jakduk.batch.processor;

import com.jakduk.batch.common.JakdukConst;
import com.jakduk.batch.model.db.ArticleComment;
import com.jakduk.batch.model.embedded.BoardLog;
import com.jakduk.batch.model.embedded.SimpleWriter;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BoardFreeCommentAddHistoryProcessor implements ItemProcessor<ArticleComment, ArticleComment> {

    @Override
    public ArticleComment process(ArticleComment item) throws Exception {

        List<BoardLog> histories = item.getLogs();

        if (CollectionUtils.isEmpty(histories))
            histories = new ArrayList<>();

        ObjectId boardFreeCommentId = new ObjectId(item.getId());
        ObjectId historyId = new ObjectId(boardFreeCommentId.getDate());

        histories.add(new BoardLog(historyId.toHexString(), JakdukConst.BOARD_FREE_COMMENT_HISTORY_TYPE.CREATE.name(), new SimpleWriter(item.getWriter())));
        item.setLogs(histories);

        List<JakdukConst.BATCH_TYPE> batchList = Optional.ofNullable(item.getBatch())
                .orElseGet(ArrayList::new);

        if (batchList.stream().noneMatch(batch -> batch.equals(JakdukConst.BATCH_TYPE.BOARD_FREE_COMMENT_ADD_HISTORY_01))) {
            batchList.add(JakdukConst.BATCH_TYPE.BOARD_FREE_COMMENT_ADD_HISTORY_01);
            item.setBatch(batchList);
        }

        log.debug("Item id:{}", item.getId());

        return item;
    }

}
