package com.jakduk.batch.model.db;

import com.jakduk.batch.common.Constants;
import com.jakduk.batch.model.embedded.ArticleStatus;
import com.jakduk.batch.model.embedded.BoardLog;
import com.jakduk.batch.model.embedded.CommonFeelingUser;
import com.jakduk.batch.model.embedded.CommonWriter;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 자유게시판 모델
 * @author pyohwan
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Document
public class Article {

	@Id
	private String id;
	private Integer seq; // 글 번호
	private String board; // 게시판
	private String category; // 말머리 코드
	private ArticleStatus status;
	private CommonWriter writer; // 작성자
	private String subject; // 글 제목
	private String content; // 글 내용
	private Integer views; // 읽음 수
	private List<CommonFeelingUser> usersLiking;
	private List<CommonFeelingUser> usersDisliking;
	private List<BoardLog> logs; // 오래된 글은 logs가 없는 경우도 있다.
	private List<Constants.BATCH_TYPE> batch;
	private String shortContent;
	private LocalDateTime lastUpdated;
	private Boolean linkedGallery;

}
