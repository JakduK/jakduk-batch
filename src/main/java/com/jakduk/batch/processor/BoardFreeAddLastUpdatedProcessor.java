package com.jakduk.batch.processor;

import com.jakduk.batch.common.JakdukConst;
import com.jakduk.batch.model.db.BoardFree;
import com.jakduk.batch.model.embedded.BoardHistory;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by pyohwanjang on 2017. 3. 12..
 */
public class BoardFreeAddLastUpdatedProcessor implements ItemProcessor<BoardFree, BoardFree> {

    @Override
    public BoardFree process(BoardFree item) throws Exception {

        // history 배열이 존재하면, 이곳에서 가장 최근 ID로 date를 뽑아온다.
        if (! ObjectUtils.isEmpty(item.getHistory())) {
            Optional<BoardHistory> oBoardHistory = item.getHistory().stream()
                    .sorted(Comparator.comparing(BoardHistory::getId).reversed())
                    .findFirst();

            if (oBoardHistory.isPresent()) {
                BoardHistory boardHistory = oBoardHistory.get();
                ObjectId objectId = new ObjectId(boardHistory.getId());

                Instant instant = Instant.ofEpochMilli(objectId.getDate().getTime());
                item.setLastUpdated(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            }
        }
        // history 배열이 없으면, item ID에서 date를 뽑아온다.
        else {
            ObjectId objectId = new ObjectId(item.getId());

            Instant instant = Instant.ofEpochMilli(objectId.getDate().getTime());
            item.setLastUpdated(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        }

        List<JakdukConst.BATCH_TYPE> batchList = Optional.ofNullable(item.getBatch())
                .orElseGet(ArrayList::new);

        if (batchList.stream().noneMatch(batch -> batch.equals(JakdukConst.BATCH_TYPE.BOARD_FREE_ADD_LAST_UPDATED_01))) {
            batchList.add(JakdukConst.BATCH_TYPE.BOARD_FREE_ADD_LAST_UPDATED_01);
            item.setBatch(batchList);
        }

        return item;
    }

}
