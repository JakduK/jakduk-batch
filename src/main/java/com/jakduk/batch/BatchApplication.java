package com.jakduk.batch;

import com.jakduk.batch.common.converter.DateToLocalDateTimeConverter;
import com.jakduk.batch.common.converter.LocalDateTimeToDateConverter;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableBatchProcessing
public class BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

	@Bean
	public CustomConversions customConversions() {
		List<Converter<?, ?>> converters = new ArrayList<>();
		converters.add(new DateToLocalDateTimeConverter());
		converters.add(new LocalDateTimeToDateConverter());
		return new CustomConversions(converters);
	}
}


