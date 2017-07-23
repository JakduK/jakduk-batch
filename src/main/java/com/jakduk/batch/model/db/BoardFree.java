package com.jakduk.batch.model.db;

import com.jakduk.batch.common.JakdukConst;
import com.jakduk.batch.model.embedded.BoardHistory;
import com.jakduk.batch.model.embedded.BoardStatus;
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
public class BoardFree {

	@Id
	private String id;

	/**
	 * 작성자
	 */
	private CommonWriter writer;
	
	/**
	 * 글 제목
	 */
	private String subject;
	
	/**
	 * 글 내용
	 */
	private String content;
	
	/**
	 * 글 번호
	 */
	private int seq;
	
	/**
	 * 분류 ID
	 */
	private JakdukConst.BOARD_CATEGORY_TYPE category;
	
	/**
	 * 조회
	 */
	private int views;
	
	private List<CommonFeelingUser> usersLiking;
	
	private List<CommonFeelingUser> usersDisliking;
	
	private BoardStatus status;
	
	private List<BoardHistory> history;

	private List<JakdukConst.BATCH_TYPE> batch;

	private String shortContent;

	private LocalDateTime lastUpdated;

	private Boolean linkedGallery;

}
