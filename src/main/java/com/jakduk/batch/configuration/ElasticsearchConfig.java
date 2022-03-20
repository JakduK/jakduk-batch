package com.jakduk.batch.configuration;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class ElasticsearchConfig {

	public final JakdukProperties.Elasticsearch elasticsearchProperties;

	@Autowired
	public ElasticsearchConfig(JakdukProperties.Elasticsearch elasticsearchProperties) {
		this.elasticsearchProperties = elasticsearchProperties;
	}

	@Bean
	public RestHighLevelClient client() {
		ClientConfiguration clientConfiguration = ClientConfiguration.builder()
			.connectedTo(elasticsearchProperties.getHostAndPort().toArray(new String[0]))
			.build();

		return RestClients.create(clientConfiguration).rest();
	}
}
