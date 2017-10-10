package com.jakduk.batch.repository;

import com.jakduk.batch.model.db.Article;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleRepository extends MongoRepository<Article, String>, ArticleRepositoryCustom {
}
