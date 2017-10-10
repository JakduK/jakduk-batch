package com.jakduk.batch.model.db;

import com.jakduk.batch.common.Constants;
import com.jakduk.batch.model.embedded.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author <a href="mailto:phjang1983@daum.net">Jang,Pyohwan</a>
 * @company  : http://jakduk.com
 * @date     : 2014. 11. 16.
 * @desc     :
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Document
public class ArticleComment {

	@Id
	private String id;
	private ArticleItem article;
	private ArticleCommentStatus status;
	private CommonWriter writer;
	private String content;
	private List<CommonFeelingUser> usersLiking;
	private List<CommonFeelingUser> usersDisliking;
	private Boolean linkedGallery;
	private List<BoardLog> logs;
    private List<Constants.BATCH_TYPE> batch;

}
