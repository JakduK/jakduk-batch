package com.jakduk.batch.model.elasticsearch;

import com.jakduk.batch.model.embedded.CommonWriter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
public class EsBoard {
	
    private String id;
	
	private CommonWriter writer;
	
	private String subject;
	
	private String content;
	
	private Integer seq;
	
	private String category;

	private List<String> galleries;

}
