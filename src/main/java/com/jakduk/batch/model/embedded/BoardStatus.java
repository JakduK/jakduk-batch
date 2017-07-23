package com.jakduk.batch.model.embedded;

import lombok.*;

/**
 * @author <a href="mailto:phjang1983@daum.net">Jang,Pyohwan</a>
 * @company  : http://jakduk.com
 * @date     : 2015. 1. 11.
 * @desc     :
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BoardStatus {
	
	private Boolean notice;
	
	private Boolean delete;
	
	private String device;

}
