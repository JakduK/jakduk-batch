package com.jakduk.batch.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 관계만 나타내기 위하여 PK 정보만 있음. 변경될 소지가 있는 필드는 없어야 한다.
 *
 * @author <a href="mailto:phjang1983@daum.net">Jang,Pyohwan</a>
 * @company  : http://jakduk.com
 * @date     : 2014. 12. 30.
 * @desc     :
 */

@Getter
@AllArgsConstructor
@ToString
public class ArticleItem {

	private String id;
	private Integer seq;
	@Setter private String board;

}
