package com.jakduk.batch.model.elasticsearch;

import java.util.List;

import com.jakduk.batch.model.embedded.CommonWriter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* @author <a href="mailto:phjang1983@daum.net">Jang,Pyohwan</a>
* @company  : http://jakduk.com
* @date     : 2015. 8. 3.
* @desc     :
*/

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class EsArticle {
	
    private String id;
	private Integer seq;
	private String board;
	private String category;
	private CommonWriter writer;
	private String subject;
	private String content;
	private List<String> galleries;
	private String boardJoinField;
}
