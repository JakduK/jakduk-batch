package com.jakduk.batch.model.elasticsearch;

import java.util.List;
import java.util.Map;

import com.jakduk.batch.model.embedded.ArticleItem;
import com.jakduk.batch.model.embedded.CommonWriter;

import lombok.Builder;
import lombok.Getter;

/**
* @author <a href="mailto:phjang1983@daum.net">Jang,Pyohwan</a>
* @company  : http://jakduk.com
* @date     : 2015. 8. 23.
* @desc     :
*/

@Builder
@Getter
public class EsComment {
	
    private String id;
	private ArticleItem article;
	private CommonWriter writer;
	private String content;
	private List<String> galleries;
	private Map<String, String> boardJoinField;

}
