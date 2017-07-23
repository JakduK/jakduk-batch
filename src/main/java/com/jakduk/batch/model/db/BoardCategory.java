package com.jakduk.batch.model.db;

import com.jakduk.batch.model.embedded.LocalSimpleName;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@Document
public class BoardCategory {

	@Id
	private String id;
	
	private String code;

	private List<LocalSimpleName> names;

}
