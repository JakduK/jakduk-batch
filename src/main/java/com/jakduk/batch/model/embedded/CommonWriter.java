package com.jakduk.batch.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통으로 사용하는 작성자
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommonWriter {
	
	private String userId;
	private String username;
	private String providerId;
	private UserPictureInfo picture;

}
