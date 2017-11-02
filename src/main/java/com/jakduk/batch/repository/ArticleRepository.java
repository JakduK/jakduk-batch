package com.jakduk.batch.repository;

import com.jakduk.batch.model.db.Article;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String>, ArticleRepositoryCustom {
    Optional<Article> findOneById(String id);
}
